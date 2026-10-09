# SOLID Analysis — ระบบแจ้งซ่อมภายในหอพัก

path อยู่ใต้ `src/main/java/com/example/dormitory/` · เลขซ้ายของโค้ดคือเลขบรรทัดจริงในไฟล์ (`...` = ตัดบรรทัดที่ไม่เกี่ยวข้อง)

## ตารางสรุป

| หลักการ | ไฟล์ | บรรทัด | เหตุผลโดยย่อ |
|---|---|---|---|
| **S** | `service/impl/NotificationServiceImpl.java` | 20–30 | จัดการการแจ้งเตือนอย่างเดียว พึ่งแค่ Repository ของ Notification และ User |
| **S** | `service/SpringSecurityService.java` | 18–24 | มีหน้าที่เดียวคือสร้าง Authentication ให้ Spring Security |
| **S** | `domain/state/impl/PendingState.java`, `CompletedState.java` | 10–23, 10–18 | แต่ละคลาสรู้กฎของสถานะเดียว |
| **S** | `domain/state/RepairRequestStateRegistry.java` | 13–37 | ค้นหาและตรวจการเปลี่ยนสถานะเท่านั้น |
| **S** | `domain/command/impl/AssignTechnicianCommand.java` | 9–31 | ห่อการกระทำเดียวคือมอบหมายงาน |
| **S** | `event/impl/RepairStatusSubjectImpl.java`, `NotificationObserver.java` | 12–52, 9–28 | Subject กระจายเหตุการณ์ Observer รับแล้วส่งต่อ แยกหน้าที่กัน |
| **S** | `repository/RepairAssignmentRepository.java` | 34–39 | มีแต่คำสั่งค้นหาข้อมูล ไม่มีตรรกะทางธุรกิจ |
| **O** | `domain/state/RepairRequestStateRegistry.java`, `impl/ApprovedState.java` | 17–22, 10–11 | เพิ่มสถานะใหม่ด้วยคลาส `@Component` ใหม่ ไม่ต้องแก้ Registry |
| **O** | `event/impl/RepairStatusSubjectImpl.java`, `NotificationObserver.java` | 16–23, 9–10 | เพิ่ม Observer ใหม่ได้โดยไม่แก้ Subject |
| **O** | `domain/command/RepairCommand.java`, `controller/web/AdminRepairRequestController.java` | 3–5, 105–107 | เพิ่ม Command ใหม่โดยไม่เปลี่ยนวิธีเรียก `execute()` |
| **O** | `service/impl/RepairRequestServiceImpl.java` | 106–112 | ตรวจสถานะผ่าน Registry ไม่ใช้ if/else ตามสถานะ |
| **L** | `domain/state/RepairRequestState.java`, `impl/CompletedState.java` | 7–19, 15–17 | State ทุกคลาสทำตามสัญญาเดียวกัน แทนกันได้ |
| **L** | `domain/state/RepairRequestStateRegistry.java` | 26–36 | ใช้ State ผ่านชนิด Interface โดยไม่เช็คชนิดจริง |
| **L** | `event/impl/RepairStatusSubjectImpl.java` | 48–52 | เรียก Observer ทุกตัวแบบเดียวกัน |
| **L** | `controller/web/AdminRepairRequestController.java` | 105–107, 130–132 | ใช้ Command ทุกตัวผ่านตัวแปรชนิด `RepairCommand` |
| **I** | `event/RepairStatusObserver.java`, `RepairAssignmentObserver.java` | 3–8, 3–8 | มีเมธอดเดียวต่อเหตุการณ์ |
| **I** | `domain/command/RepairCommand.java` | 3–5 | มีเมธอดเดียวคือ `execute()` |
| **I** | `service/AdminReporterService.java`, `AdminTechnicianService.java` | 10–15, 11–17 | แยก Interface ตามเอนทิตี |
| **I** | `service/ReporterProfileService.java`, `TechnicianProfileService.java` | 8–13, 6–9 | Interface เล็ก มี 1–2 เมธอด |
| **D** | `service/impl/RepairRequestServiceImpl.java` | 46–62 | พึ่ง `RepairStatusSubject` ซึ่งเป็น Interface |
| **D** | `event/impl/AssignmentNotificationObserver.java` | 13–19 | พึ่ง `NotificationService` ซึ่งเป็น Interface |
| **D** | `domain/command/impl/ConfirmCompletionCommand.java`, `controller/web/AdminWorkflowController.java` | 15–24, 22–29 | พึ่ง `RepairRequestService` ซึ่งเป็น Interface |
| **D** | `repository/RepairRequestRepository.java` | 13 | Service พึ่ง Repository ที่เป็น Interface |

รายละเอียดและโค้ดของแต่ละข้ออยู่ด้านล่าง ท้ายเอกสารมีหัวข้อจุดที่ยังไม่เป็นไปตาม SOLID

## S — Single Responsibility (หนึ่งคลาส หนึ่งหน้าที่)

#### S-1 `NotificationServiceImpl` — จัดการการแจ้งเตือนอย่างเดียว

คลาสนี้รู้จักแค่ `NotificationRepository` และ `UserRepository` ทำหน้าที่สร้าง อ่าน นับยังไม่อ่าน และทำเครื่องหมายอ่านแล้ว ไม่ยุ่งกับการเปลี่ยนสถานะคำร้อง

```java
// ===== service/impl/NotificationServiceImpl.java
  20 | public class NotificationServiceImpl
  21 |         implements NotificationService {
  22 | 
  23 |     private final NotificationRepository notificationRepository;
  24 |     private final UserRepository userRepository;
  25 | 
  26 |     public NotificationServiceImpl(
  27 |             NotificationRepository notificationRepository,
  28 |             UserRepository userRepository) {
  29 | 
  30 |         this.notificationRepository = notificationRepository;
```

#### S-2 `SpringSecurityService` — หน้าที่เดียวคือสร้าง Authentication

พึ่งแค่ `UserRepository` และมีเหตุผลเดียวที่จะแก้ คือวิธีผูกผู้ใช้เข้ากับ Spring Security

```java
// ===== service/SpringSecurityService.java
  18 | @Service
  19 | public class SpringSecurityService {
  20 |     private final UserRepository userRepository;
  21 | 
  22 |     public SpringSecurityService(UserRepository userRepository) {
  23 |         this.userRepository = userRepository;
  24 |     }
```

#### S-3 State แต่ละคลาสรู้กฎของสถานะเดียว

`PendingState` บอกเพียงว่า PENDING ไปต่อได้ที่ APPROVED หรือ REJECTED ส่วน `CompletedState` เป็นสถานะปลายทาง คลาสอื่น (`ApprovedState`, `InProgressState`, `RejectedState`, `InCompletedState`) ทำแบบเดียวกัน กฎของสถานะหนึ่งเปลี่ยนต้องแก้คลาสเดียว

```java
// ===== domain/state/impl/PendingState.java
  10 | @Component 
  11 | public class PendingState implements RepairRequestState {
  12 |     @Override
  13 |     public RepairRequestStatus getStatus() {
  14 |         return RepairRequestStatus.PENDING;
  15 |     }
  16 | 
  17 |     @Override
  18 |     public Set<RepairRequestStatus> getAllowedNext() {
  19 |        return Set.of(
  20 |             RepairRequestStatus.APPROVED,
  21 |             RepairRequestStatus.REJECTED
  22 |        );
  23 |     }
// ===== domain/state/impl/CompletedState.java
  10 | @Component
  11 | public class CompletedState implements RepairRequestState {
  12 |     @Override public RepairRequestStatus getStatus() {
  13 |         return RepairRequestStatus.COMPLETED;
  14 |     }
  15 |     @Override public Set<RepairRequestStatus> getAllowedNext() {
  16 |         return Set.of();
  17 |     }
  18 | }
```

#### S-4 `RepairRequestStateRegistry` — ค้นหาและตรวจ State เท่านั้น

รวบรวม State ทั้งหมดและตอบคำถามว่า "เปลี่ยนสถานะนี้ได้ไหม" ไม่มี logic ธุรกิจอื่นปนอยู่

```java
// ===== domain/state/RepairRequestStateRegistry.java
  13 | @Component 
  14 | public class RepairRequestStateRegistry {
  15 |     private final Map<RepairRequestStatus,RepairRequestState> byStatus;
  16 | 
  17 |     public RepairRequestStateRegistry(List<RepairRequestState> states){
  18 |         this.byStatus = states.stream()
  19 |             .collect(Collectors.toMap(
  20 |                 RepairRequestState::getStatus,
  21 |                 Function.identity()
  22 |             ));
  23 |     }
  24 | 
  25 |     // ค้นหา state จาก enum 
  26 |     public RepairRequestState get(RepairRequestStatus status){
  27 |         RepairRequestState s = byStatus.get(status);
  28 |         if(s == null){
  29 |             throw new IllegalArgumentException("ไม่พบ state : " + status);
  30 |         }
  31 |         return  s; // state object
  32 |     }
  33 | 
  34 |     // ตรวจสอบว่าเปลี่ยนสถานะได้หรือไม่
  35 |     public boolean canTransition(RepairRequestStatus from, RepairRequestStatus to){
  36 |         return  get(from).canTransitionTo(to);
  37 |     }
```

#### S-5 Command ห่อการกระทำเดียว

`AssignTechnicianCommand` มีหน้าที่เดียวคือสั่ง "มอบหมายงาน" (`ConfirmCompletionCommand` ทำแบบเดียวกันกับ "ยืนยันงานเสร็จ")

```java
// ===== domain/command/impl/AssignTechnicianCommand.java
   9 | public class AssignTechnicianCommand implements RepairCommand {
  10 | 
  11 |     private final RepairAssignmentService repairAssignmentService;
  12 |     private final UUID repairRequestId;
  13 |     private final UUID technicianId;
  14 |     private final UUID adminId;
  15 |     private final String adminNote;
  16 | 
  17 |     public AssignTechnicianCommand(RepairAssignmentService repairAssignmentService,
  18 |                                     UUID repairRequestId, UUID technicianId,
  19 |                                     UUID adminId, String adminNote) {
  20 |         this.repairAssignmentService = repairAssignmentService;
  21 |         this.repairRequestId = repairRequestId;
  22 |         this.technicianId = technicianId;
  23 |         this.adminId = adminId;
  24 |         this.adminNote = adminNote;
  25 |     }
  26 | 
  27 |     @Override
  28 |     public void execute() {
  29 |         repairAssignmentService.assignTechnician(repairRequestId, technicianId, adminId, adminNote);
  30 |     }
  31 | }
```

#### S-6 Subject / Observer แยกหน้าที่ชัด

`RepairStatusSubjectImpl` ดูแลรายชื่อ observer และกระจาย event ส่วน `NotificationObserver` รับ event แล้วส่งต่อให้ `NotificationService` ไม่มี logic อื่น

```java
// ===== event/impl/RepairStatusSubjectImpl.java
  12 | @Component
  13 | public class RepairStatusSubjectImpl
  14 |         implements RepairStatusSubject {
  15 | 
  16 |     private final List<RepairStatusObserver> observers;
  17 | 
  18 |     public RepairStatusSubjectImpl(
  19 |             List<RepairStatusObserver> observers) {
  20 | 
  21 |         this.observers =
  22 |                 new ArrayList<>(observers);
  23 |     }
     ...
  39 |     @Override
  40 |     public void notifyObservers(RepairStatusChangedEvent event) {
     ...
  48 |         for (RepairStatusObserver observer
  49 |                 : observers) {
  50 | 
  51 |             observer.onStatusChanged(event);
  52 |         }
// ===== event/impl/NotificationObserver.java
   9 | @Component
  10 | public class NotificationObserver implements RepairStatusObserver {
  11 | 
  12 |     private final NotificationService notificationService;
  13 | 
  14 |     public NotificationObserver(
  15 |             NotificationService notificationService) {
  16 |         this.notificationService = notificationService;
  17 |     }
  18 | 
  19 |     @Override
  20 |     public void onStatusChanged(
  21 |             RepairStatusChangedEvent event) {
     ...
  27 |         notificationService.notifyStatusChanged(event);
  28 |     }
```

#### S-7 Repository เข้าถึงข้อมูลอย่างเดียว

ทุก repository เป็น interface ที่มีแต่ query ไม่มี business logic

```java
// ===== repository/RepairAssignmentRepository.java
  34 |     Optional<RepairAssignment> findByRepairRequest_RepairRequestId(UUID repairRequestId);
  35 |     
  36 |     // ตรวจสอบ RepairRequest ว่ามี assignment ผูกอยู่หรือไม่
  37 |     boolean existsByRepairRequest_RepairRequestId(UUID repairRequestId);
  38 | 
  39 |     List<RepairAssignment> findByTechnician_TechnicianIdOrderByAssignDateDesc(UUID technicianId);
```

## O — Open/Closed (เปิดให้ขยาย ปิดไม่ให้แก้)

#### O-1 เพิ่มสถานะใหม่ = เพิ่มคลาส `@Component` ใหม่

Registry รับ `List<RepairRequestState>` ที่ Spring รวบรวมให้อัตโนมัติ เพิ่มสถานะใหม่ไม่ต้องแก้ Registry หรือ service ที่เรียกใช้ ทั้งนี้ยังต้องเพิ่มค่าใน enum `RepairRequestStatus` และระบุสถานะใหม่ใน `getAllowedNext()` ของสถานะก่อนหน้า

```java
// ===== domain/state/RepairRequestStateRegistry.java
  17 |     public RepairRequestStateRegistry(List<RepairRequestState> states){
  18 |         this.byStatus = states.stream()
  19 |             .collect(Collectors.toMap(
  20 |                 RepairRequestState::getStatus,
  21 |                 Function.identity()
  22 |             ));
// ===== domain/state/impl/ApprovedState.java
  10 | @Component
  11 | public class ApprovedState implements RepairRequestState {
```

#### O-2 เพิ่มผู้รับแจ้งเหตุใหม่ = เพิ่ม `@Component` ที่ implement Observer

`RepairStatusSubjectImpl` รับ `List<RepairStatusObserver>` จาก Spring (`RepairAssignmentSubjectImpl` ทำแบบเดียวกันที่ 16-20) จึงเพิ่ม observer ได้โดยไม่แก้ Subject

```java
// ===== event/impl/RepairStatusSubjectImpl.java
  16 |     private final List<RepairStatusObserver> observers;
  17 | 
  18 |     public RepairStatusSubjectImpl(
  19 |             List<RepairStatusObserver> observers) {
  20 | 
  21 |         this.observers =
  22 |                 new ArrayList<>(observers);
  23 |     }
// ===== event/impl/NotificationObserver.java
   9 | @Component
  10 | public class NotificationObserver implements RepairStatusObserver {
```

#### O-3 เรียกคำสั่งผ่าน `RepairCommand` เดียว

controller เรียก `execute()` เหมือนกันทุกคำสั่ง เพิ่ม Command ใหม่ได้โดยไม่เปลี่ยนวิธีเรียก

```java
// ===== domain/command/RepairCommand.java
   3 | public interface RepairCommand {
   4 |     void execute();
   5 | }
// ===== controller/web/AdminRepairRequestController.java
 105 |         RepairCommand command = new AssignTechnicianCommand(
 106 |                 repairAssignmentService, id, technicianId, adminId, adminNote);
 107 |         command.execute();
```

#### O-4 ตรวจการเปลี่ยนสถานะผ่าน Registry ไม่ใช้ if/else

กฎเปลี่ยนที่ State อย่างเดียว ไม่ต้องแก้เมธอด `changeStatus`

```java
// ===== service/impl/RepairRequestServiceImpl.java
 106 |                 RepairRequestStatus previousStatus = request.getStatus();
 107 |                 
 108 |                 // State Pattern
 109 |                 if (!repairRequestStateRegistry.canTransition(previousStatus, newStatus)) {
 110 |                         throw new BusinessException(
 111 |                                 "ไม่สามารถเปลี่ยนจาก " + previousStatus + " เป็น " + newStatus + " ได้");
 112 |                 }
```

## L — Liskov Substitution (คลาสลูกแทนที่แม่ได้)

หมายเหตุ: โปรเจคไม่มีการสืบทอดคลาสลึก หลักการนี้ปรากฏผ่าน interface เป็นหลัก

#### L-1 State ทั้ง 6 คลาสแทนกันได้

ทุก State ทำตามสัญญาเดียวกัน: `getAllowedNext()` คืน `Set` ที่ไม่เป็น null สถานะปลายทางคืน `Set.of()` ไม่ throw exception `canTransitionTo` และ `isFinal` เป็น default method ที่ใช้ร่วมกัน จึงให้ผลสม่ำเสมอทุกคลาส

```java
// ===== domain/state/RepairRequestState.java
   7 | public interface RepairRequestState {
   8 |     RepairRequestStatus getStatus();
   9 | 
  10 |     Set<RepairRequestStatus> getAllowedNext();
  11 | 
  12 |     // ตรวจสอบว่าสามารถเปลี่ยนสถานะคำร้องได้หรือไม่
  13 |     default boolean canTransitionTo(RepairRequestStatus next){
  14 |         return next != null && getAllowedNext().contains(next);
  15 |     }
  16 | 
  17 |     default boolean isFinal(){
  18 |         return getAllowedNext().isEmpty();
  19 |     }
// ===== domain/state/impl/CompletedState.java
  15 |     @Override public Set<RepairRequestStatus> getAllowedNext() {
  16 |         return Set.of();
  17 |     }
```

#### L-2 Registry ใช้ State ผ่านชนิด interface ไม่รู้ชนิดจริง

ดึง `RepairRequestState` ออกมาใช้ได้ทุกตัวโดยไม่ต้องเช็คชนิด

```java
// ===== domain/state/RepairRequestStateRegistry.java
  26 |     public RepairRequestState get(RepairRequestStatus status){
  27 |         RepairRequestState s = byStatus.get(status);
  28 |         if(s == null){
  29 |             throw new IllegalArgumentException("ไม่พบ state : " + status);
  30 |         }
  31 |         return  s; // state object
  32 |     }
  33 | 
  34 |     // ตรวจสอบว่าเปลี่ยนสถานะได้หรือไม่
  35 |     public boolean canTransition(RepairRequestStatus from, RepairRequestStatus to){
  36 |         return  get(from).canTransitionTo(to);
```

#### L-3 Subject เรียก Observer ทุกตัวแบบเดียวกัน

ไม่มีการเช็คชนิด observer ก่อนเรียก ตัวไหนก็แทนกันได้

```java
// ===== event/impl/RepairStatusSubjectImpl.java
  48 |         for (RepairStatusObserver observer
  49 |                 : observers) {
  50 | 
  51 |             observer.onStatusChanged(event);
  52 |         }
```

#### L-4 Command ถูกใช้ผ่านตัวแปรชนิด `RepairCommand`

ทั้ง `AssignTechnicianCommand` และ `ConfirmCompletionCommand` ถูกสร้างเป็น `RepairCommand` แล้วเรียก `execute()` โดย controller ไม่ต้องรู้ชนิดจริงหลังสร้าง

```java
// ===== controller/web/AdminRepairRequestController.java
 105 |         RepairCommand command = new AssignTechnicianCommand(
 106 |                 repairAssignmentService, id, technicianId, adminId, adminNote);
 107 |         command.execute();
     ...
 130 |         RepairCommand command = new ConfirmCompletionCommand(
 131 |                 repairRequestService, id, adminId, note);
 132 |         command.execute();
```

## I — Interface Segregation (interface เล็ก เฉพาะเรื่อง)

#### I-1 Observer interface มีเมธอดเดียวต่อเหตุการณ์

ผู้ implement ไม่ต้องเขียนเมธอดที่ไม่เกี่ยวข้อง

```java
// ===== event/RepairStatusObserver.java
   3 | public interface RepairStatusObserver {
   4 | 
   5 |     void onStatusChanged(
   6 |             RepairStatusChangedEvent event
   7 |     );
   8 | }
// ===== event/RepairAssignmentObserver.java
   3 | public interface RepairAssignmentObserver {
   4 | 
   5 |     void onAssignmentCreated(
   6 |             RepairAssignmentCreatedEvent event
   7 |     );
   8 | }
```

#### I-2 `RepairCommand` มีเมธอดเดียว

Command ทุกตัวต้องทำแค่ `execute()`

```java
// ===== domain/command/RepairCommand.java
   3 | public interface RepairCommand {
   4 |     void execute();
   5 | }
```

#### I-3 Admin service แยกตามเอนทิตี

`AdminReporterService` และ `AdminTechnicianService` แยกจากกัน controller ของแต่ละเอนทิตีพึ่งเฉพาะ interface ของตัวเอง (`AdminResidentService` ที่ 11-17 ก็เช่นกัน)

```java
// ===== service/AdminReporterService.java
  10 | public interface AdminReporterService {
  11 |     List<AdminReporterResponse> getAllReporters();
  12 |     AdminReporterResponse getReporterById(UUID reporterId);
  13 |     AdminReporterResponse updateReporter(UUID reporterId, AdminReporterUpdateRequest request);
  14 |     List<RepairRequestHistoryResponse> getRepairHistoryByReporterId(UUID reporterId);
  15 | }
// ===== service/AdminTechnicianService.java
  11 | public interface AdminTechnicianService {
  12 |     List<AdminTechnicianResponse> getAllTechnicians();
  13 |     AdminTechnicianResponse getTechnicianById(UUID technicianId);
  14 |     AdminTechnicianResponse updateTechnician(UUID technicianId, AdminTechnicianUpdateRequest request);
  15 |     AdminTechnicianResponse createTechnician(AdminTechnicianCreateRequest request);
  16 |     List<AdminTechnicianHistoryResponse> getRepairHistoryByTechnicianId(UUID technicianId);
  17 | }
```

#### I-4 Profile service เล็กและเฉพาะเรื่อง

interface เหล่านี้มี 1-2 เมธอด

```java
// ===== service/ReporterProfileService.java
   8 | public interface ReporterProfileService {
   9 | 
  10 |     Reporter getReporterByUserId(UUID userId);
  11 | 
  12 |     void updatePhone(UUID userId, String phoneNo);
  13 | }
// ===== service/TechnicianProfileService.java
   6 | public interface TechnicianProfileService {
   7 | 
   8 |     Technician getTechnicianByUserId(UUID userId);
   9 | }
```

## D — Dependency Inversion (พึ่ง interface ไม่พึ่งคลาสจริง)

#### D-1 `RepairRequestServiceImpl` พึ่ง `RepairStatusSubject` (interface)

รับผ่าน constructor โดยไม่รู้จัก `RepairStatusSubjectImpl`

```java
// ===== service/impl/RepairRequestServiceImpl.java
  46 |         private final RepairStatusSubject repairStatusSubject;
     ...
  48 |         @Autowired
  49 |         public RepairRequestServiceImpl(RepairRequestRepository repairRequestRepository,
     ...
  55 |                         RepairStatusSubject repairStatusSubject) {
     ...
  62 |                 this.repairStatusSubject = repairStatusSubject;
```

#### D-2 `AssignmentNotificationObserver` พึ่ง `NotificationService` (interface)

ไม่ผูกกับ `NotificationServiceImpl` จึงเปลี่ยนวิธีส่งการแจ้งเตือนได้โดยไม่ต้องแก้ Observer

```java
// ===== event/impl/AssignmentNotificationObserver.java
  13 |     private final NotificationService notificationService;
  14 | 
  15 |     public AssignmentNotificationObserver(
  16 |             NotificationService notificationService) {
  17 | 
  18 |         this.notificationService = notificationService;
  19 |     }
```

#### D-3 `ConfirmCompletionCommand` และ `AdminWorkflowController` พึ่ง `RepairRequestService` (interface)

สลับ implementation ได้โดยไม่แก้ผู้เรียก

```java
// ===== domain/command/impl/ConfirmCompletionCommand.java
  15 |     private final RepairRequestService repairRequestService;
     ...
  20 |     public ConfirmCompletionCommand(RepairRequestService repairRequestService,
  21 |                                      UUID repairRequestId,
  22 |                                      UUID adminId,
  23 |                                      String note) {
  24 |         this.repairRequestService = repairRequestService;
// ===== controller/web/AdminWorkflowController.java
  22 |     private final RepairRequestService repairRequestService;
     ...
  26 |     public AdminWorkflowController(RepairRequestService repairRequestService,
  27 |                                    RepairAssignmentService repairAssignmentService) {
  28 |         this.repairRequestService = repairRequestService;
  29 |         this.repairAssignmentService = repairAssignmentService;
```

#### D-4 Repository เป็น interface

service พึ่ง interface ที่ Spring Data สร้างตัวจริงให้

```java
// ===== repository/RepairRequestRepository.java
  13 | public interface RepairRequestRepository extends JpaRepository<RepairRequest, UUID> {
```

## จุดที่ยังไม่เป็นไปตาม SOLID

#### X-1 `RepairAssignmentService` ทำหลายหน้าที่ในคลาสเดียว (ขัด S)

คลาสเดียวรวมการดึงงานประจำวัน แปลง Entity เป็น DTO สรุปยอดงาน ปรับปรุงสถานะงาน และมอบหมายงาน เมื่อแก้เรื่องใดเรื่องหนึ่งต้องแก้คลาสนี้ ควรแยกเป็นคลาสย่อย เช่น `TechnicianJobQueryService` และ `RepairAssignmentCommandService`

```java
// ===== service/RepairAssignmentService.java
  41 | public class RepairAssignmentService {
     ...
  97 |     // DAILY JOBS
     ...
 148 |     // FIND ASSIGNMENTS
     ...
 232 |     // DTO MAPPING
     ...
 388 |     // UPDATE JOB STATUS
     ...
 495 |     // DAILY SUMMARY
```

#### X-2 แปลง Filter เป็นสถานะด้วย `switch` (ขัด O)

เมื่อเพิ่มค่าใน `RepairJobFilter` ต้องกลับมาแก้เมธอดนี้ทุกครั้ง ควรเก็บสถานะที่คู่กันไว้ใน enum `RepairJobFilter` เอง

```java
// ===== service/RepairAssignmentService.java
 208 | private RepairRequestStatus getStatusFromFilter(
 209 |         RepairJobFilter filter
 210 | ) {
 211 | 
 212 |     return switch (filter) {
```

#### X-3 พึ่งคลาสจริงที่ไม่มี interface (ขัด D)

`RepairAssignmentService` และ `AuthService` ไม่มี interface ทำให้ Controller และ Command ต้องพึ่งคลาสจริงโดยตรง เปลี่ยน implementation หรือ mock ตอนทดสอบได้ยากกว่า

```java
// ===== domain/command/impl/AssignTechnicianCommand.java
  11 |     private final RepairAssignmentService repairAssignmentService;
// ===== controller/web/AdminRepairRequestController.java
  27 |     private final RepairAssignmentService repairAssignmentService;
// ===== service/AuthService.java
  18 | public class AuthService {
```
