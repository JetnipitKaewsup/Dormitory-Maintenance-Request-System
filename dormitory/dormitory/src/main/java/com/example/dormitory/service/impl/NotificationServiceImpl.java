package com.example.dormitory.service.impl;

import com.example.dormitory.domain.entity.Admin;
import com.example.dormitory.domain.entity.Notification;
import com.example.dormitory.domain.entity.RepairAssignment;
import com.example.dormitory.domain.entity.RepairRequest;
import com.example.dormitory.domain.entity.User;
import com.example.dormitory.domain.enums.NotificationType;
import com.example.dormitory.domain.enums.RepairRequestStatus;
import com.example.dormitory.dto.response.NotificationResponse;
import com.example.dormitory.event.RepairAssignmentCreatedEvent;
import com.example.dormitory.event.RepairStatusChangedEvent;
import com.example.dormitory.repository.AdminRepository;
import com.example.dormitory.repository.NotificationRepository;
import com.example.dormitory.repository.RepairAssignmentRepository;
import com.example.dormitory.repository.RepairRequestRepository;
import com.example.dormitory.repository.UserRepository;
import com.example.dormitory.service.NotificationService;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class NotificationServiceImpl
        implements NotificationService {

    private final NotificationRepository notificationRepository;

    private final UserRepository userRepository;

    private final RepairRequestRepository repairRequestRepository;

    private final AdminRepository adminRepository;

    private final RepairAssignmentRepository repairAssignmentRepository;


    public NotificationServiceImpl(
            NotificationRepository notificationRepository,
            UserRepository userRepository,
            RepairRequestRepository repairRequestRepository,
            AdminRepository adminRepository,
            RepairAssignmentRepository repairAssignmentRepository) {

        this.notificationRepository =
                notificationRepository;

        this.userRepository =
                userRepository;

        this.repairRequestRepository =
                repairRequestRepository;

        this.adminRepository =
                adminRepository;

        this.repairAssignmentRepository =
                repairAssignmentRepository;
    }


    // =========================================================
    // TECHNICIAN : แจ้งเตือนเมื่อ Admin มอบหมายงาน
    // =========================================================

    @Override
    public void notifyAssignmentCreated(
            RepairAssignmentCreatedEvent event) {

        RepairAssignment assignment =
                repairAssignmentRepository
                        .findById(event.assignmentId())
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "ไม่พบข้อมูลงานที่มอบหมาย"
                                )
                        );


        User technician =
                assignment
                        .getTechnician()
                        .getUser();


        RepairRequest request =
                assignment
                        .getRepairRequest();


        String message =
                "ได้รับงาน: "
                        + formatDateTime(
                                assignment.getAssignDate()
                        )
                        + "\n\n"

                        + "เวลาที่ต้องไปซ่อม: "
                        + formatDateTime(request.getStartDateTime())
                        + " - "
                        + formatDateTime(request.getEndDateTime())
                        + "\n\n"

                        + "อาการ:\n"
                        + request.getDescription()
                        + "\n\n"

                        + "สถานที่:\n"
                        + getRoomLocation(request);


        Notification notification =
                new Notification(
                        technician,
                        NotificationType.NEW_ASSIGNMENT,
                        "งานใหม่",
                        message,
                        assignment.getAssignmentId()
                );


        notificationRepository.save(notification);
    }


    // =========================================================
    // GET NOTIFICATIONS
    // =========================================================

    @Override
    public List<NotificationResponse> getNotifications(
            UUID userId) {

        return notificationRepository
                .findByRecipient_UserIdOrderByCreatedAtDesc(
                        userId
                )
                .stream()
                .map(notification ->
                        new NotificationResponse(
                                notification.getNotificationId(),
                                notification.getType().name(),
                                notification.getTitle(),
                                notification.getMessage(),
                                notification.getReferenceId(),
                                notification.isRead(),
                                notification.getCreatedAt()
                        )
                )
                .toList();
    }


    // =========================================================
    // COUNT UNREAD
    // =========================================================

    @Override
    public long countUnread(
            UUID userId) {

        return notificationRepository
                .countByRecipient_UserIdAndReadFalse(
                        userId
                );
    }


    // =========================================================
    // MARK AS READ
    // =========================================================

    @Override
    public void markAsRead(
            UUID notificationId,
            UUID userId) {

        Notification notification =
                notificationRepository
                        .findByNotificationIdAndRecipient_UserId(
                                notificationId,
                                userId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "ไม่พบ Notification"
                                )
                        );


        notification.markAsRead();
    }


    // =========================================================
    // STATUS CHANGED
    // =========================================================


@Override
public void notifyStatusChanged(RepairStatusChangedEvent event) {

    // ADMIN เปลี่ยนสถานะคำร้องหลัก
    if ("ADMIN".equals(event.changedByRole())) {
        switch (event.newStatus()) {
            case APPROVED -> notifyReporter(
                    event,
                    NotificationType.STATUS_APPROVED,
                    "คำร้องได้รับการอนุมัติ"
            );

            case REJECTED -> notifyReporter(
                    event,
                    NotificationType.STATUS_REJECTED,
                    "คำร้องถูกปฏิเสธ"
            );

            case COMPLETED -> {
                notifyReporter(
                        event,
                        NotificationType.STATUS_COMPLETED,
                        "Admin ยืนยันว่าซ่อมเสร็จแล้ว"
                );
                notifyTechnicianAdminConfirmed(event);
            }

            case IN_COMPLETED -> {
                notifyReporter(
                        event,
                        NotificationType.STATUS_IN_COMPLETED,
                        "Admin ยืนยันว่าซ่อมไม่สำเร็จ"
                );
                notifyTechnicianAdminConfirmed(event);
            }

            default -> {
            }
        }
    }

    // REPORTER ส่งคำร้องใหม่
    if ("REPORTER".equals(event.changedByRole())
            && event.newStatus() == RepairRequestStatus.PENDING) {
        notifyAdminsNewRequest(event);
    }

    // TECHNICIAN รายงานผลของ Assignment
    // แจ้ง Admin เท่านั้น ไม่แจ้ง Reporter ในขั้นตอนนี้
    if ("TECHNICIAN".equals(event.changedByRole())) {
        switch (event.newStatus()) {
            case COMPLETED, IN_COMPLETED ->
                    notifyAdminsTechnicianStatusChanged(event);

            default -> {
            }
        }
    }
}


    // =========================================================
    // NOTIFY REPORTER
    // =========================================================

    private void notifyReporter(
            RepairStatusChangedEvent event,
            NotificationType type,
            String title) {


        RepairRequest request =
                repairRequestRepository
                        .findById(
                                event.repairRequestId()
                        )
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "ไม่พบคำร้องแจ้งซ่อม"
                                )
                        );


        User reporter =
                request
                        .getReporter()
                        .getUser();


        String message =
                buildRequestInfo(request);


        Notification notification =
                new Notification(
                        reporter,
                        type,
                        title,
                        message,
                        request.getRepairRequestId()
                );


        notificationRepository.save(notification);
    }


    // =========================================================
    // BUILD REQUEST INFORMATION
    // =========================================================

    private String buildRequestInfo(
            RepairRequest request) {

        return "เวลา: "
                + formatDateTime(
                        request.getCreatedAt()
                )
                + "\n\n"

                + "อาการ:\n"
                + request.getDescription()
                + "\n\n"

                + "สถานที่:\n"
                + getRoomLocation(request);
    }


    // =========================================================
    // ADMIN : มีคำร้องใหม่
    // =========================================================

    private void notifyAdminsNewRequest(
            RepairStatusChangedEvent event) {


        RepairRequest request =
                repairRequestRepository
                        .findById(
                                event.repairRequestId()
                        )
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "ไม่พบคำร้องแจ้งซ่อม"
                                )
                        );


        String message =
                buildRequestInfo(request);


        List<Admin> admins =
                adminRepository.findAll();


        for (Admin admin : admins) {

            Notification notification =
                    new Notification(
                            admin.getUser(),
                            NotificationType.NEW_REQUEST,
                            "มีคำร้องแจ้งซ่อมใหม่",
                            message,
                            request.getRepairRequestId()
                    );


            notificationRepository.save(
                    notification
            );
        }
    }


    // =========================================================
    // ADMIN : TECHNICIAN เปลี่ยนสถานะ
    // =========================================================

    private void notifyAdminsTechnicianStatusChanged(RepairStatusChangedEvent event) {


        RepairRequest request =
                repairRequestRepository
                        .findById(
                                event.repairRequestId()
                        )
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "ไม่พบคำร้องแจ้งซ่อม"
                                )
                        );


        User technician =
                userRepository
                        .findById(
                                event.changedBy()
                        )
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "ไม่พบข้อมูลช่าง"
                                )
                        );


        String technicianName =
                technician.getFirstName()
                        + " "
                        + technician.getLastName();
  
                boolean completed =
                        event.newStatus() == RepairRequestStatus.COMPLETED;

                String title = completed
                        ? "ช่างรายงานว่าซ่อมเสร็จแล้ว - รอตรวจสอบ"
                        : "ช่างรายงานว่าซ่อมไม่สำเร็จ - รอตรวจสอบ";

                NotificationType type = completed
                        ? NotificationType.STATUS_COMPLETED
                        : NotificationType.STATUS_IN_COMPLETED;

                String message =
                        "ช่าง: " + technicianName
                        + "\n\n"
                        + "เวลารายงานผล: "
                        + formatDateTime(LocalDateTime.now())
                        + "\n\n"
                        + "ผลการซ่อม:\n"
                        + (event.note() == null || event.note().isBlank()
                                ? "-"
                                : event.note())
                        + "\n\n"
                        + "อาการ:\n"
                        + request.getDescription()
                        + "\n\n"
                        + "สถานที่:\n"
                        + getRoomLocation(request)
                        + "\n\n"
                        + "กรุณาตรวจสอบผลก่อนยืนยันสถานะคำร้อง";


        List<Admin> admins =
                adminRepository.findAll();


        for (Admin admin : admins) {

            Notification notification =
                    new Notification(
                            admin.getUser(),
                            type,
                            title,
                            message,
                            request.getRepairRequestId()
                    );


            notificationRepository.save(
                    notification
            );
        }
    }

        //notifyช่างหลังการซ่อมได้รับการยืนยัน  

                private void notifyTechnicianAdminConfirmed(
                        RepairStatusChangedEvent event) {

                RepairRequest request = repairRequestRepository
                        .findById(event.repairRequestId())
                        .orElseThrow(() ->
                                new IllegalStateException("ไม่พบคำร้องแจ้งซ่อม"));

                RepairAssignment assignment = repairAssignmentRepository
                        .findByRepairRequest_RepairRequestId(
                                event.repairRequestId())
                        .orElseThrow(() ->
                                new IllegalStateException("ไม่พบงานที่มอบหมาย"));

                boolean completed =
                        event.newStatus() == RepairRequestStatus.COMPLETED;

                String title = completed
                        ? "Admin ยืนยันผลการซ่อม: สำเร็จ"
                        : "Admin ยืนยันผลการซ่อม: ไม่สำเร็จ";

                String message = (completed
                        ? "Admin ยืนยันว่าซ่อมเสร็จแล้ว"
                        : "Admin ยืนยันว่าซ่อมไม่สำเร็จ")
                        + "\n\nอาการ:\n" + request.getDescription()
                        + "\n\nสถานที่:\n" + getRoomLocation(request);

                notificationRepository.save(new Notification(
                        assignment.getTechnician().getUser(),
                        completed
                                ? NotificationType.STATUS_COMPLETED
                                : NotificationType.STATUS_IN_COMPLETED,
                        title,
                        message,
                        event.repairRequestId()
                ));
                }


    // =========================================================
    // FORMAT DATE TIME
    // =========================================================

    private static final DateTimeFormatter
            DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern(
                    "dd/MM/yyyy HH:mm"
            );


    private String formatDateTime(
            LocalDateTime dateTime) {

        if (dateTime == null) {
            return "-";
        }


        return dateTime.format(
                DATE_TIME_FORMATTER
        );
    }


    // =========================================================
    // GET ROOM LOCATION
    // =========================================================

    private String getRoomLocation(
            RepairRequest request) {

        if (request.getRoom() == null) {
            return "-";
        }


        String buildingName =
                request
                        .getRoom()
                        .getBuilding()
                        .getBuildingName();


        return "ห้อง "
                + request.getRoom().getRoomNo()
                + " "
                + buildingName;
    }
}