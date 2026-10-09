# Design Patterns — ระบบแจ้งซ่อมภายในหอพัก

สรุป Design Pattern ที่ใช้ในระบบ (package `com.example.dormitory`)

| Pattern | ปัญหาที่แก้ | ไฟล์/คลาสที่ใช้ | Class Diagram ประกอบ |
|---|---|---|---|
| **Enterprise / Architectural Patterns** | | | |
| Layered Architecture | แยกหน้าที่ของโค้ดเป็นชั้น ไม่ให้ส่วนติดต่อผู้ใช้ ตรรกะทางธุรกิจ และการเข้าถึงข้อมูลปะปนกัน โดยชั้นบนเรียกใช้ชั้นล่างเท่านั้น | `controller/web`, `controller/api` → `service`, `service/impl` → `repository` → `domain/entity` | |
| MVC | แยกการรับคำขอ ข้อมูล และหน้าจอแสดงผลออกจากกัน | Controller: `AuthController`, `ReporterController`, `AdminRepairRequestController`, `AdminWorkflowController`, `RepairWorkController`, `NotificationController`, `AdminReporterWebController`, `AdminResidentWebController`, `AdminTechnicianWebController` / Model: `domain/entity/*` / View: Thymeleaf `resources/templates/{admin,reporter,technician}/*` | |
| Repository Pattern | ซ่อนรายละเอียดการติดต่อฐานข้อมูล ให้ Service เรียกใช้ผ่าน Interface ที่สืบทอดจาก `JpaRepository` โดยไม่ต้องเขียน SQL เอง | `RepairRequestRepository`, `RepairRequestStatusHistoryRepository`, `RepairAssignmentRepository`, `RepairAssignmentStatusHistoryRepository`, `NotificationRepository`, `UserRepository`, `AdminRepository`, `ReporterRepository`, `TechnicianRepository`, `RoomRepository`, `AdminReporterRepository`, `AdminResidentRepository`, `AdminTechnicianRepository` | |
| Service Layer Pattern | รวมตรรกะทางธุรกิจไว้ในชั้นเดียว ไม่กระจายอยู่ใน Controller และให้ทั้งหน้าเว็บและ REST API ใช้ตรรกะชุดเดียวกัน | Interface + Impl: `RepairRequestService`/`RepairRequestServiceImpl`, `NotificationService`/`NotificationServiceImpl`, `TechnicianService`/`TechnicianServiceImpl`, `TechnicianProfileService`/`TechnicianProfileServiceImpl`, `ReporterProfileService`/`ReporterProfileServiceImpl`, `AdminTechnicianService`/`AdminTechnicianServiceImpl`, `AdminReporterService`/`AdminReporterServiceImpl`, `AdminResidentService`/`AdminResidentServiceImpl` / คลาสเดี่ยว: `RepairAssignmentService`, `AuthService`, `ReporterService`, `SpringSecurityService` | |
| DTO Pattern | ไม่ส่ง Entity ออกไปยัง API หรือฟอร์มโดยตรง และกำหนดรูปแบบข้อมูลเข้า-ออกให้ชัดเจน | Request: `dto/request/*` (`LoginRequest`, `RegisterRequest`, `CreateRepairRequestDto`, `AdminTechnicianCreateRequest` ฯลฯ), `dto/*Form` (`RepairRequestForm`, `ProfileForm`) / Response: `dto/response/*` (`RepairRequestResponse`, `DailyRepairJobDto`, `NotificationResponse` ฯลฯ) / การแปลง: `RepairRequestResponse.from()` และ `toResponse()` ใน Service | |
| Dependency Injection (Constructor Injection) | ลดการผูกติดระหว่างคลาส (Loose Coupling) และทดสอบได้ง่าย โดย Spring Container สร้าง Bean และส่งเข้า Constructor ให้ | Controller และ Service ทุกคลาส เช่น `AdminRepairRequestController`, `RepairAssignmentService`, `RepairRequestServiceImpl`, `RepairStatusSubjectImpl` (รับ `List<RepairStatusObserver>`), `RepairRequestStateRegistry` (รับ `List<RepairRequestState>`) | |
| **GoF — Behavioral Patterns** | | | |
| State Pattern | ควบคุมการเปลี่ยนสถานะของคำร้องให้เป็นไปตามกฎ (เช่น อนุมัติได้เฉพาะ PENDING) โดยไม่ต้องเขียน if/else กระจายทั่วโค้ด | `domain/state/RepairRequestState`, `domain/state/RepairRequestStateRegistry`, `domain/state/impl/*` (`PendingState`, `ApprovedState`, `RejectedState`, `InProgressState`, `CompletedState`, `InCompletedState`), enum `RepairRequestStatus` / ใช้ใน `RepairRequestServiceImpl`, `RepairAssignmentService` | |
| Command Pattern | ห่อหุ้มคำสั่งของผู้ดูแลระบบ (มอบหมายงาน, ยืนยันผลการซ่อม) เป็นออบเจกต์ และเรียกใช้ผ่าน Interface เดียวกัน | Command: `domain/command/RepairCommand` / Concrete Command: `domain/command/impl/AssignTechnicianCommand`, `ConfirmCompletionCommand` / Invoker: `AdminRepairRequestController` / Receiver: `RepairAssignmentService`, `RepairRequestService` | |
| Observer Pattern | แจ้งเหตุการณ์ไปยังส่วนที่เกี่ยวข้องเมื่อคำร้องเปลี่ยนสถานะหรือมีการมอบหมายงาน โดย Service ไม่ต้องรู้จักผู้รับโดยตรง | Subject: `event/RepairStatusSubject`, `event/RepairAssignmentSubject`, `event/impl/RepairStatusSubjectImpl`, `event/impl/RepairAssignmentSubjectImpl` / Observer: `event/RepairStatusObserver`, `event/RepairAssignmentObserver`, `event/impl/NotificationObserver`, `event/impl/AssignmentNotificationObserver` / Event: `event/RepairStatusChangedEvent`, `event/RepairAssignmentCreatedEvent` / ผู้แจ้งเหตุการณ์: `RepairRequestServiceImpl`, `RepairAssignmentService` | |

---

## 1. Layered Architecture

ระบบจัดโครงสร้างโครงงานแบบแบ่งชั้น โดยชั้นบนเรียกใช้ชั้นล่างเท่านั้น และ Controller ไม่เรียก Repository โดยตรง

| ชั้น | แพ็กเกจ | หน้าที่ |
|---|---|---|
| Presentation | `controller/web`, `controller/api`, `resources/templates` | รับคำขอจากผู้ใช้และแสดงผล |
| Business | `service`, `service/impl`, `domain/state`, `domain/command`, `event` | ตรรกะทางธุรกิจ กฎการเปลี่ยนสถานะ และการแจ้งเหตุการณ์ |
| Data Access | `repository` | ติดต่อฐานข้อมูลผ่าน Spring Data JPA |
| Domain | `domain/entity`, `domain/enums` | โครงสร้างข้อมูลหลักของระบบ |

**Class Diagram:** 

## 2. MVC

ระบบใช้ Spring MVC ร่วมกับ Thymeleaf โดยแบ่งหน้าที่ดังนี้

- **Model** คือ Entity ใน `domain/entity` เช่น `RepairRequest`, `RepairAssignment`, `User`
- **View** คือ Template ใน `resources/templates` แยกตามบทบาท ได้แก่ `admin/`, `reporter/` และ `technician/`
- **Controller** คือคลาสใน `controller/web` ทำหน้าที่รับคำขอ เรียก Service และส่งชื่อ View กลับไป

| Controller | หน้าที่ |
|---|---|
| `AuthController` | สมัครสมาชิก เข้าสู่ระบบ ลืมรหัสผ่าน |
| `ReporterController` | ผู้แจ้งส่ง ดู ติดตาม และลบคำร้อง |
| `AdminRepairRequestController` | ผู้ดูแลระบบดู อนุมัติ ปฏิเสธ มอบหมายงาน และยืนยันผลการซ่อม |
| `AdminWorkflowController` | ผู้ดูแลระบบดูรายการงานที่รอมอบหมายและรอตรวจสอบ |
| `RepairWorkController` | ช่างดูงานประจำวันและปรับปรุงสถานะงาน |
| `NotificationController` | ช่างดูการแจ้งเตือนและทำเครื่องหมายว่าอ่านแล้ว |
| `AdminReporterWebController`, `AdminResidentWebController`, `AdminTechnicianWebController` | ผู้ดูแลระบบจัดการข้อมูลผู้แจ้ง ผู้พักอาศัย และช่าง |

**Class Diagram:** 

## 3. Repository Pattern

ทุก Repository สืบทอดจาก `JpaRepository` ของ Spring Data JPA ทำให้ได้คำสั่งพื้นฐาน เช่น `save`, `findById`, `findAll`, `delete` โดยไม่ต้องเขียน SQL เอง และสามารถประกาศเมธอดค้นหาจากชื่อเมธอดได้ เช่น `findByReporter_ReporterIdOrderByCreatedAtDesc` หรือ `findByTechnician_TechnicianIdAndAssignDateBetween`

Repository ในระบบ: `UserRepository`, `AdminRepository`, `ReporterRepository`, `TechnicianRepository`, `RoomRepository`, `RepairRequestRepository`, `RepairRequestStatusHistoryRepository`, `RepairAssignmentRepository`, `RepairAssignmentStatusHistoryRepository`, `NotificationRepository`, `AdminReporterRepository`, `AdminResidentRepository`, `AdminTechnicianRepository`

**Class Diagram:** 

## 4. Service Layer Pattern

ตรรกะทางธุรกิจทั้งหมดอยู่ในชั้น Service ทำให้ Controller ฝั่งหน้าเว็บ (`controller/web`) และฝั่ง REST API (`controller/api`) เรียกใช้ตรรกะชุดเดียวกันได้ Service ส่วนใหญ่แยกเป็น Interface และคลาส Implementation

| Interface | Implementation |
|---|---|
| `RepairRequestService` | `RepairRequestServiceImpl` |
| `NotificationService` | `NotificationServiceImpl` |
| `TechnicianService` | `TechnicianServiceImpl` |
| `TechnicianProfileService` | `TechnicianProfileServiceImpl` |
| `ReporterProfileService` | `ReporterProfileServiceImpl` |
| `AdminTechnicianService` | `AdminTechnicianServiceImpl` |
| `AdminReporterService` | `AdminReporterServiceImpl` |
| `AdminResidentService` | `AdminResidentServiceImpl` |

ส่วน `RepairAssignmentService`, `AuthService`, `ReporterService` และ `SpringSecurityService` เป็นคลาส Service ที่ไม่ได้แยก Interface

**Class Diagram:** 

## 5. DTO Pattern

ระบบใช้ DTO แยกข้อมูลที่รับเข้าและส่งออกจาก Entity

- **Request DTO / Form** สำหรับข้อมูลที่รับเข้า เช่น `LoginRequest`, `RegisterRequest`, `CreateRepairRequestDto`, `RepairRequestForm`, `AdminTechnicianCreateRequest`, `AdminResidentCreateRequest`
- **Response DTO** สำหรับข้อมูลที่ส่งออก เช่น `RepairRequestResponse`, `ReporterResponse`, `AdminTechnicianResponse`, `DailyRepairJobDto`, `NotificationResponse`

การแปลง Entity เป็น DTO ทำได้ 2 วิธีในระบบ ได้แก่

1. เมธอด static `from()` ใน DTO เช่น `RepairRequestResponse.from(RepairRequest)`
2. เมธอด `toResponse()` ภายใน Service เช่น `AdminTechnicianServiceImpl` และ `AdminResidentServiceImpl`

**Class Diagram:** 

## 6. Dependency Injection

ทุก Controller และ Service รับ Dependency ผ่าน Constructor (Constructor Injection) โดย Spring Container เป็นผู้สร้าง Bean และส่งเข้ามาให้ ไม่มีการใช้คำสั่ง `new` สร้าง Service หรือ Repository ในโค้ดทางธุรกิจ

นอกจากนี้ Spring ยังส่ง Bean หลายตัวเข้ามาพร้อมกันในรูปแบบ `List` ได้ ซึ่งระบบใช้ความสามารถนี้ใน State Pattern (`List<RepairRequestState>`) และ Observer Pattern (`List<RepairStatusObserver>`) เพื่อให้เพิ่ม State หรือ Observer ใหม่ได้โดยไม่ต้องแก้คลาสเดิม

**Class Diagram:** 

## 7. State Pattern

### ปัญหา

คำร้องแจ้งซ่อมเปลี่ยนสถานะได้ตามลำดับที่กำหนดเท่านั้น เช่น อนุมัติได้เฉพาะคำร้องที่อยู่ในสถานะ PENDING หากเขียนเงื่อนไขด้วย if/else ในทุก Service โค้ดจะซ้ำซ้อนและแก้ไขยาก

### โครงสร้าง

| บทบาท | คลาส |
|---|---|
| State (Interface) | `RepairRequestState` กำหนด `getStatus()`, `getAllowedNext()`, `canTransitionTo()` และ `isFinal()` |
| Concrete State | `PendingState`, `ApprovedState`, `RejectedState`, `InProgressState`, `CompletedState`, `InCompletedState` |
| Context / Registry | `RepairRequestStateRegistry` รวบรวม State ทั้งหมดและเลือก State ตามค่าสถานะ |
| ค่าสถานะ | enum `RepairRequestStatus` |

### กฎการเปลี่ยนสถานะ

| สถานะปัจจุบัน | เปลี่ยนเป็นได้ |
|---|---|
| PENDING | APPROVED, REJECTED |
| APPROVED | IN_PROGRESS |
| IN_PROGRESS | COMPLETED, IN_COMPLETED |
| REJECTED | — (สถานะสุดท้าย) |
| COMPLETED | — (สถานะสุดท้าย) |
| IN_COMPLETED | — (สถานะสุดท้าย) |

### การใช้งาน

- `RepairRequestServiceImpl.changeStatus()` เรียก `repairRequestStateRegistry.canTransition(previous, next)` ก่อนเปลี่ยนสถานะคำร้อง หากไม่ผ่านจะโยน `BusinessException`
- `RepairAssignmentService.updateJobStatus()` ใช้ State ชุดเดียวกันตรวจสอบสถานะงานของช่าง ทำให้ช่างเปลี่ยนงานจาก IN_PROGRESS ได้เฉพาะ COMPLETED หรือ IN_COMPLETED

**Class Diagram:** 

## 8. Command Pattern

### ปัญหา

การกระทำของผู้ดูแลระบบ เช่น การมอบหมายงานและการยืนยันผลการซ่อม ต้องเรียก Service คนละตัวและใช้ข้อมูลต่างกัน การห่อแต่ละการกระทำเป็นออบเจกต์ช่วยให้ Controller เรียกใช้ผ่าน Interface เดียวกัน และเพิ่มคำสั่งใหม่ได้โดยไม่ต้องแก้ Controller มาก

### โครงสร้าง

| บทบาท | คลาส |
|---|---|
| Command (Interface) | `RepairCommand` มีเมธอด `execute()` |
| Concrete Command | `AssignTechnicianCommand` มอบหมายงานให้ช่าง / `ConfirmCompletionCommand` ยืนยันผลการซ่อม (เปลี่ยนคำร้องเป็น COMPLETED) |
| Receiver | `RepairAssignmentService` (รับคำสั่งมอบหมายงาน) / `RepairRequestService` (รับคำสั่งยืนยันผล) |
| Client และ Invoker | `AdminRepairRequestController` สร้าง Command และเรียก `execute()` |

### การใช้งาน

- `POST /admin/requests/{id}/assign` สร้าง `AssignTechnicianCommand` แล้วเรียก `execute()` ซึ่งจะเรียก `RepairAssignmentService.assignTechnician()`
- `POST /admin/requests/{id}/inspect` สร้าง `ConfirmCompletionCommand` แล้วเรียก `execute()` ซึ่งจะเรียก `RepairRequestService.adminUpdateStatus(..., COMPLETED, ...)`

**Class Diagram:** 

## 9. Observer Pattern

### ปัญหา

เมื่อคำร้องเปลี่ยนสถานะหรือมีการมอบหมายงาน ระบบต้องแจ้งเตือนผู้ที่เกี่ยวข้อง หาก Service เรียกส่วนแจ้งเตือนโดยตรง Service จะผูกติดกับผู้รับทุกราย Observer Pattern ทำให้ Service เพียงแจ้งเหตุการณ์ไปยัง Subject แล้ว Subject ส่งต่อให้ Observer ทุกตัวที่ลงทะเบียนไว้

### โครงสร้าง

ระบบมี Subject และ Observer 2 ชุด แยกตามประเภทเหตุการณ์

| บทบาท | ชุดที่ 1: การเปลี่ยนสถานะ | ชุดที่ 2: การมอบหมายงาน |
|---|---|---|
| Event | `RepairStatusChangedEvent` (รหัสคำร้อง รหัสงาน สถานะเดิม สถานะใหม่ ผู้เปลี่ยน บทบาท หมายเหตุ) | `RepairAssignmentCreatedEvent` (รหัสงาน รหัสคำร้อง รหัสผู้ใช้ของช่าง รหัสผู้ดูแลระบบ) |
| Subject (Interface) | `RepairStatusSubject` | `RepairAssignmentSubject` |
| Concrete Subject | `RepairStatusSubjectImpl` | `RepairAssignmentSubjectImpl` |
| Observer (Interface) | `RepairStatusObserver` มีเมธอด `onStatusChanged()` | `RepairAssignmentObserver` มีเมธอด `onAssignmentCreated()` |
| Concrete Observer | `NotificationObserver` | `AssignmentNotificationObserver` |

Subject มีเมธอด `addObserver()`, `removeObserver()` และ `notifyObservers()` โดย Spring ส่ง Observer ทุกตัวที่เป็น `@Component` เข้ามาใน Constructor ของ Subject โดยอัตโนมัติ เมื่อต้องการเพิ่มผู้รับเหตุการณ์ใหม่ เช่น ส่งอีเมล ก็เพียงสร้างคลาสใหม่ที่ implement Observer โดยไม่ต้องแก้ Service

### จุดที่แจ้งเหตุการณ์

| เหตุการณ์ | เมธอดที่แจ้ง | Subject |
|---|---|---|
| ผู้แจ้งส่งคำร้องใหม่ (→ PENDING) | `RepairRequestServiceImpl.createRequest()` | `RepairStatusSubject` |
| ผู้ดูแลระบบอนุมัติ ปฏิเสธ หรือยืนยันผล | `RepairRequestServiceImpl.changeStatus()` | `RepairStatusSubject` |
| ผู้ดูแลระบบมอบหมายงาน (คำร้อง APPROVED → IN_PROGRESS) | `RepairAssignmentService.assignTechnician()` ผ่าน `changeStatus()` | `RepairStatusSubject` |
| สร้างงานมอบหมายให้ช่าง | `RepairAssignmentService.assignTechnician()` | `RepairAssignmentSubject` |
| ช่างปรับปรุงสถานะงาน (→ COMPLETED / IN_COMPLETED) | `RepairAssignmentService.updateJobStatus()` | `RepairStatusSubject` |

Observer ทั้งสองตัวส่งต่อเหตุการณ์ให้ `NotificationService` ซึ่งบันทึกข้อมูลการแจ้งเตือน (`Notification`) ลงฐานข้อมูล เช่น เมื่อมีการมอบหมายงาน ระบบจะสร้างการแจ้งเตือนประเภท `NEW_ASSIGNMENT` ถึงช่างที่ได้รับงาน และช่างดูการแจ้งเตือนได้ผ่าน `NotificationController`

**Class Diagram:**
