# ระบบแจ้งซ่อมภายในหอพัก (Dormitory Maintenance Request System)

ระบบแจ้งซ่อมภายในหอพักเป็นเว็บแอปพลิเคชันสำหรับจัดการคำร้องแจ้งซ่อมตั้งแต่ต้นจนจบ ผู้พักอาศัยส่งคำร้องแจ้งซ่อมพร้อมระบุช่วงเวลาที่สะดวกและติดตามสถานะได้ด้วยตนเอง
ผู้ดูแลระบบพิจารณาอนุมัติหรือปฏิเสธคำร้อง มอบหมายงานให้ช่าง และยืนยันการปิดงาน
ช่างดูรายการงานประจำวันและบันทึกผลการซ่อม พร้อมรับการแจ้งเตือนเมื่อได้รับงานใหม่
ทุกการเปลี่ยนสถานะถูกบันทึกเป็นประวัติเพื่อใช้ตรวจสอบย้อนหลัง

> รายวิชา Principles of Software Design · College of Computing, Khon Kaen University

---

## สมาชิกกลุ่ม

| ลำดับ | ชื่อ-นามสกุล | รหัสนักศึกษา | Section | Branch | หน้าที่รับผิดชอบ |
|---|---|---|---|---|---|
| 1 | นายรพีภัทร แก้วคูณ | 673380056-7 | 1 | rapeephat_673380056-7_01 | จัดการระบบ จัดการช่าง , ผู้แจ้ง , ผู้พักอาศัย ฝั่งฝั่งผู้ดูแลระบบ |
| 2 | นางสาวสุชาดา ศรีจักรโคตร | 673380068-0 | 1 | suchada_673380068-0_01 | จัดการระบบฝั่งช่าง และ ระบบแจ้งเตือน (Observer Pattern)|
| 3 | นางสาวพิไลพร คำเวียง | 673380286-0 | 2 | pilaiphon_67330286-0_02 | จัดการระบบสถานะคำร้องกับสถานะงาน ฝั่งผู้ดูแลระบบ |
| 4 | นางสาวหทัยพัทธ วิสุทธิธรรม | 673380297-5 | 2 | hathaipat_673380297-5_02 | จัดการระบบ ยืนยันตัวตน , การเปลี่ยนรหัสผ่าน และ ทดสอบระบบ |
| 5 | นางสาวเจตนิพิฐ แก้วทรัพย์ | 673380299-1 | 2 | jetnipit_673380299-1_02 | ส่งคำร้องแจ้งซ่อมใหม่ ดึงรายการแจ้งซ่อมฝั่งผู้แจ้ง (State Pattern) |

---

## รายงานโครงงาน

📄 [รายงานระบบแจ้งซ่อมภายในหอพัก](https://github.com/JetnipitKaewsup/Dormitory-Maintenance-Request-System/blob/main/doc/%E0%B8%A3%E0%B8%B2%E0%B8%A2%E0%B8%87%E0%B8%B2%E0%B8%99%E0%B8%A3%E0%B8%B0%E0%B8%9A%E0%B8%9A%E0%B9%81%E0%B8%88%E0%B9%89%E0%B8%87%E0%B8%8B%E0%B9%88%E0%B8%AD%E0%B8%A1%E0%B9%83%E0%B8%99%E0%B8%AB%E0%B8%AD%E0%B8%9E%E0%B8%B1%E0%B8%81.pdf)

---

## Tech Stack

| ส่วน | เทคโนโลยี |
|---|---|
| ภาษา | Java 17 |
| Backend Framework | Spring Boot 4.1 (Spring MVC, Spring Data JPA, Spring Security, Bean Validation) |
| Frontend | Thymeleaf, HTML, CSS |
| Database | PostgreSQL บน Supabase |
| Authentication | Supabase Auth ร่วมกับ Spring Security |
| Database Migration | Flyway |
| API Documentation | springdoc-openapi (Swagger UI) |
| Build Tool | Maven (Maven Wrapper) |
| Testing | JUnit 5, Spring Boot Test, Mockito |
| Container / Deployment | Docker, Railway |

---

## System Architecture

ระบบใช้สถาปัตยกรรมแบบแบ่งชั้น (Layered Architecture) ร่วมกับรูปแบบ MVC โดยชั้นบนเรียกใช้ชั้นล่างเท่านั้น

```
┌─────────────────────────────────────────────────────────┐
│  Browser (Thymeleaf pages)        REST Client / Swagger  │
└──────────────┬──────────────────────────────┬───────────┘
               │                              │
┌──────────────▼──────────────┐  ┌────────────▼───────────┐
│  controller/web              │  │  controller/api         │   Presentation
└──────────────┬──────────────┘  └────────────┬───────────┘
               └──────────────┬───────────────┘
┌─────────────────────────────▼───────────────────────────┐
│  service / service.impl                                  │   Business
│  domain/state (State) · domain/command (Command)         │
│  event (Observer)                                        │
└─────────────────────────────┬───────────────────────────┘
┌─────────────────────────────▼───────────────────────────┐
│  repository (Spring Data JPA)                            │   Data Access
└─────────────────────────────┬───────────────────────────┘
┌─────────────────────────────▼───────────────────────────┐
│  PostgreSQL (Supabase)          Supabase Auth            │   External
└─────────────────────────────────────────────────────────┘
```

| ชั้น | หน้าที่ |
|---|---|
| Presentation | รับคำขอจากผู้ใช้ ตรวจสิทธิ์ตามบทบาท (ADMIN, REPORTER, TECHNICIAN) และแสดงผล |
| Business | ตรรกะทางธุรกิจ กฎการเปลี่ยนสถานะของคำร้อง และการแจ้งเหตุการณ์ |
| Data Access | อ่านและบันทึกข้อมูลผ่าน Repository |

Design Pattern ที่ใช้ ได้แก่ Layered Architecture, MVC, Repository, Service Layer, DTO, Dependency Injection, State, Command และ Observer รายละเอียดอยู่ใน [doc/design-patterns.md](doc/design-patterns.md) และการวิเคราะห์ตามหลัก SOLID อยู่ใน [doc/solid-analysis.md](doc/solid-analysis.md)

### สถานะของคำร้องแจ้งซ่อม

```
PENDING ──► APPROVED ──► IN_PROGRESS ──► COMPLETED
   │                          │
   └──► REJECTED              └──► IN_COMPLETED
```

| สถานะ | ความหมาย |
|---|---|
| `PENDING` | รอผู้ดูแลระบบพิจารณา |
| `APPROVED` | อนุมัติแล้ว รอมอบหมายงาน |
| `REJECTED` | ไม่อนุมัติ |
| `IN_PROGRESS` | มอบหมายงานให้ช่างแล้ว กำลังดำเนินการ |
| `COMPLETED` | ดำเนินการเสร็จสิ้น |
| `IN_COMPLETED` | ดำเนินการไม่สำเร็จ |

---

## Database Design (ER Diagram)

<!-- ใส่ภาพ ER Diagram เช่น ![ER Diagram](doc/images/er-diagram.png) -->

| ตาราง | คำอธิบาย |
|---|---|
| `users` | บัญชีผู้ใช้ทุกบทบาท (ADMIN, REPORTER, TECHNICIAN) |
| `admin` | ข้อมูลผู้ดูแลระบบ เชื่อมกับ `users` แบบ 1:1 |
| `reporter` | ข้อมูลผู้แจ้ง เชื่อมกับ `users` และ `resident` แบบ 1:1 |
| `technician` | ข้อมูลช่างและความเชี่ยวชาญ เชื่อมกับ `users` แบบ 1:1 |
| `resident` | ข้อมูลผู้พักอาศัยและห้องพัก |
| `building` | อาคารในหอพัก |
| `room` | ห้องพักในแต่ละอาคาร |
| `repair_request` | คำร้องแจ้งซ่อม |
| `repair_request_status_history` | ประวัติการเปลี่ยนสถานะของคำร้อง |
| `repair_assignment` | งานที่มอบหมายให้ช่าง |
| `repair_assignment_status_history` | ประวัติการเปลี่ยนสถานะของงานที่มอบหมาย |
| `notification` | การแจ้งเตือนถึงผู้ใช้งาน |

โครงสร้างตารางทั้งหมดอยู่ใน `dormitory/dormitory/src/main/resources/db/migration/V1__init_schema.sql`

---

## Installation & Setup

### สิ่งที่ต้องมี

- JDK 17 ขึ้นไป
- Git
- โปรเจกต์ Supabase (ฐานข้อมูล PostgreSQL และ Auth)
- Docker (ไม่บังคับ ใช้เมื่อต้องการรันผ่าน container)

ไม่ต้องติดตั้ง Maven แยก เพราะโปรเจกต์มี Maven Wrapper (`mvnw`) มาให้แล้ว

### 1. Clone โปรเจกต์

```bash
git clone <!-- ใส่ลิงก์ repository -->
cd Dormitory-Maintenance-Request-System/dormitory/dormitory
```

### 2. ตั้งค่า Environment

สร้างไฟล์ `src/main/resources/application.properties` แล้วกำหนดค่าดังนี้

```properties
spring.datasource.url=jdbc:postgresql://<SUPABASE_DB_HOST>:5432/postgres
spring.datasource.username=<DB_USERNAME>
spring.datasource.password=<DB_PASSWORD>

supabase.url=https://<PROJECT_ID>.supabase.co
supabase.key=<SUPABASE_ANON_KEY>
```

> ไฟล์นี้มีรหัสผ่านและคีย์ลับ ห้าม commit ขึ้น repository

---

## How to Run

### รันด้วย Maven

```bash
./mvnw spring-boot:run        # macOS / Linux
mvnw.cmd spring-boot:run      # Windows
```

### รันด้วย Docker

```bash
docker compose up --build
```

เมื่อรันแล้วเปิดใช้งานได้ที่

| รายการ | URL |
|---|---|
| หน้าเว็บ | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui/index.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |

---

## API Documentation

เอกสาร API ทั้งหมดสร้างอัตโนมัติด้วย springdoc-openapi ดูและทดลองเรียกได้ที่ Swagger UI (`/swagger-ui/index.html`)

| Path | คำอธิบาย | สิทธิ์ |
|---|---|---|
| `/api/v1/repair-requests/**` | สร้าง ดู และจัดการคำร้องแจ้งซ่อม | ผู้ใช้ที่เข้าสู่ระบบ |
| `/api/v1/reporters/**` | ข้อมูลและคำร้องของผู้แจ้ง | REPORTER |
| `/api/admin/reporters/**` | จัดการข้อมูลผู้แจ้ง | ผู้ใช้ที่เข้าสู่ระบบ |
| `/api/admin/residents/**` | จัดการข้อมูลผู้พักอาศัย | ผู้ใช้ที่เข้าสู่ระบบ |
| `/api/admin/technicians/**` | จัดการข้อมูลช่าง | ผู้ใช้ที่เข้าสู่ระบบ |
| `/api/v1/auth/reset-password` | ตั้งรหัสผ่านใหม่ | ไม่ต้องเข้าสู่ระบบ |

---

## How to Run Tests

```bash
./mvnw test                   # macOS / Linux
mvnw.cmd test                 # Windows
```

ชุดทดสอบปัจจุบันคือ `DormitoryApplicationTests` ซึ่งตรวจว่า Spring Context โหลดได้ครบ จึงต้องตั้งค่า `application.properties` ให้เชื่อมต่อฐานข้อมูลได้ก่อนรันทดสอบ

ผลการทดสอบอยู่ที่ `target/surefire-reports/`

---

## Deployment URL

ระบบ Deploy บน Railway โดย build จาก `Dockerfile` (multi-stage build: Maven สำหรับ build และ JRE 17 สำหรับรัน) และเชื่อมต่อฐานข้อมูล PostgreSQL บน Supabase

| รายการ | URL |
|---|---|
| เว็บไซต์ | https://dormitory-maintenance-request-system-production-dd00.up.railway.app/ |
| Swagger UI | https://dormitory-maintenance-request-system-production-dd00.up.railway.app/swagger-ui/index.html  |

---

## Project Structure

```
Dormitory-Maintenance-Request-System/
├── README.md
├── doc/
│   ├── design-patterns.md
│   └── solid-analysis.md
└── dormitory/dormitory/
    ├── Dockerfile
    ├── docker-compose.yml
    ├── pom.xml
    ├── mvnw, mvnw.cmd
    └── src/
        ├── main/
        │   ├── java/com/example/dormitory/
        │   │   ├── config/            # Security, Supabase, OpenAPI
        │   │   ├── controller/
        │   │   │   ├── web/           # Controller ของหน้าเว็บ (Thymeleaf)
        │   │   │   └── api/           # REST API
        │   │   ├── service/           # ตรรกะทางธุรกิจ
        │   │   │   └── impl/
        │   │   ├── repository/        # Spring Data JPA
        │   │   ├── domain/
        │   │   │   ├── entity/        # Entity ของฐานข้อมูล
        │   │   │   ├── enums/         # สถานะ ประเภทงานซ่อม บทบาท
        │   │   │   ├── state/         # State Pattern
        │   │   │   └── command/       # Command Pattern
        │   │   ├── event/             # Observer Pattern
        │   │   ├── dto/               # Request / Response DTO
        │   │   ├── exception/         # จัดการข้อผิดพลาด
        │   │   └── util/
        │   └── resources/
        │       ├── db/migration/      # Flyway migration
        │       ├── templates/         # admin/, reporter/, technician/
        │       └── static/css/
        └── test/java/com/example/dormitory/
```
