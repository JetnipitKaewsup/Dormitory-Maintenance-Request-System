package com.example.dormitory.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.dormitory.domain.entity.Technician;
import com.example.dormitory.domain.entity.User;
import com.example.dormitory.dto.request.AdminTechnicianCreateRequest;
import com.example.dormitory.dto.response.AdminTechnicianResponse;
import com.example.dormitory.repository.AdminTechnicianRepository;
import com.example.dormitory.repository.RepairAssignmentRepository;
import com.example.dormitory.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AdminTechnicianServiceTS18UnitTest {

    @Mock
    private AdminTechnicianRepository technicianRepository;

    @Mock
    private UserRepository userRepository;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private WebClient supabaseWebClient;

    @Mock
    private RepairAssignmentRepository repairAssignmentRepository;

    private AdminTechnicianServiceImpl service;

    private AdminTechnicianCreateRequest request;

    private final UUID supabaseUserId =
            UUID.fromString("11111111-1111-1111-1111-111111111111");

    @BeforeEach
    void setUp() {
        service = new AdminTechnicianServiceImpl(
                technicianRepository,
                userRepository,
                supabaseWebClient,
                repairAssignmentRepository
        );

        request = new AdminTechnicianCreateRequest();
        request.setEmail("technician@example.com");
        request.setUsername("tech001");
        request.setPassword("password123");
        request.setFirstName("สมชาย");
        request.setLastName("ใจดี");
        request.setPhoneNo("0812345678");
        request.setSpecialization("ไฟฟ้า");
    }

    private void mockSupabaseResponse(Map<String, Object> response) {
        when(supabaseWebClient.post()
                .uri("/auth/v1/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(any(Map.class))
                .retrieve()
                .bodyToMono(Map.class)
                .block())
                .thenReturn(response);
    }

    @Test
    void TC_UT_18_01_createTechnicianSuccessfully() {
        when(userRepository.findByUsername("tech001"))
                .thenReturn(Optional.empty());

        Map<String, Object> supabaseResponse = new HashMap<>();
        supabaseResponse.put("id", supabaseUserId.toString());
        mockSupabaseResponse(supabaseResponse);

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(technicianRepository.save(any(Technician.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AdminTechnicianResponse response =
                service.createTechnician(request);

        assertNotNull(response);
        assertEquals("tech001", response.getUsername());
        assertEquals("สมชาย", response.getFirstName());
        assertEquals("ใจดี", response.getLastName());
        assertEquals("0812345678", response.getPhoneNo());
        assertEquals("ไฟฟ้า", response.getSpecialization());
        assertEquals(supabaseUserId, response.getUserId());

        verify(userRepository).save(any(User.class));
        verify(technicianRepository).save(any(Technician.class));
    }

    @Test
    void TC_UT_18_02_rejectDuplicateUsername() {
        when(userRepository.findByUsername("tech001"))
                .thenReturn(Optional.of(new User()));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> service.createTechnician(request)
        );

        assertTrue(exception.getMessage().contains("username"));

        verify(supabaseWebClient, never()).post();
        verify(userRepository, never()).save(any(User.class));
        verify(technicianRepository, never()).save(any(Technician.class));
    }

    @Test
    void TC_UT_18_03_rejectSupabaseResponseWithoutUserId() {
        when(userRepository.findByUsername("tech001"))
                .thenReturn(Optional.empty());

        mockSupabaseResponse(new HashMap<>());

        assertThrows(
                RuntimeException.class,
                () -> service.createTechnician(request)
        );

        verify(userRepository, never()).save(any(User.class));
        verify(technicianRepository, never()).save(any(Technician.class));
    }

    @Test
    void TC_UT_18_04_propagateSupabaseError() {
        when(userRepository.findByUsername("tech001"))
                .thenReturn(Optional.empty());

        when(supabaseWebClient.post()
                .uri("/auth/v1/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(any(Map.class))
                .retrieve()
                .bodyToMono(Map.class)
                .block())
                .thenThrow(new RuntimeException("Supabase unavailable"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> service.createTechnician(request)
        );

        assertEquals("Supabase unavailable", exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
        verify(technicianRepository, never()).save(any(Technician.class));
    }
}
