package com.example.dormitory.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import com.example.dormitory.exception.ErrorResponse;
import com.example.dormitory.util.DateTimeUtil;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice(basePackages = "com.example.dormitory.controller.api")
public class GlobalExceptionHandler {

        // 400 - Validation error (@Valid)
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
                List<String> details = ex.getBindingResult().getFieldErrors()
                                .stream()
                                .map(FieldError::getDefaultMessage)
                                .toList();

                return ResponseEntity.badRequest().body(
                                new ErrorResponse("VALIDATION_ERROR",
                                                "ข้อมูลไม่ถูกต้อง",
                                                400,
                                                DateTimeUtil.now(),
                                                details));
        }

        // 400 - IllegalArgument (จาก Service)
        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException ex) {
                return ResponseEntity.badRequest()
                                .body(new ErrorResponse("BAD_REQUEST", ex.getMessage(), 400));
        }

        // 404 - ไม่พบ resource
        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new ErrorResponse("NOT_FOUND", ex.getMessage(), 404));
        }

        // 409 - Business rule violation (เช่น ยกเลิกได้เฉพาะ PENDING)
        @ExceptionHandler({ BusinessException.class, IllegalStateException.class })
        public ResponseEntity<ErrorResponse> handleConflict(RuntimeException ex) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                                .body(new ErrorResponse("CONFLICT", ex.getMessage(), 409));
        }

        // 500 - ที่เหลือทั้งหมด
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(new ErrorResponse("INTERNAL_ERROR",
                                                "เกิดข้อผิดพลาดภายในระบบ: " + ex.getMessage(),
                                                500));
        }

        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ErrorResponse> handleInvalidEnum(HttpMessageNotReadableException ex) {
                String msg = "ข้อมูลไม่ถูกต้อง";
                if (ex.getMessage().contains("RepairRequestStatus")) {
                        msg = "สถานะไม่ถูกต้อง";
                }
                return ResponseEntity.badRequest()
                                .body(new ErrorResponse("INVALID_FORMAT", msg, 400));
        }
}