# ระบบแจ้งซ่อมภายในหอพัก (Dormitory Maintenance Request System)

เว็บแอปพลิเคชันสำหรับแจ้งซ่อมและติดตามงานซ่อมภายในหอพัก ผู้พักอาศัยส่งคำร้องแจ้งซ่อมผ่านระบบ ผู้ดูแลระบบพิจารณาและมอบหมายงานให้ช่าง ช่างอัปเดตผลการซ่อม และผู้ดูแลระบบยืนยันการปิดงาน ทุกขั้นตอนบันทึกประวัติสถานะไว้ตรวจสอบย้อนหลังได้

> รายวิชา Principles of Software Design · College of Computing, Khon Kaen University

## 🔗 ลิงก์

| รายการ | ลิงก์ |
|---|---|
| เว็บไซต์ (Deploy บน Railway) | <!-- ใส่ลิงก์ deploy --> |
| API Documentation (Swagger UI) | <!-- ใส่ลิงก์ deploy -->/swagger-ui/index.html |
| Design Patterns | [doc/design-patterns.md](doc/design-patterns.md) |
| SOLID Analysis | [doc/solid-analysis.md](doc/solid-analysis.md) |

## ผู้ใช้งานและความสามารถของระบบ

| บทบาท | ความสามารถ |
|---|---|
| **ผู้แจ้ง (Reporter)** | สมัครสมาชิกและเข้าสู่ระบบ · ส่งคำร้องแจ้งซ่อมพร้อมระบุช่วงเวลาที่สะดวก · ดูประวัติและติดตามสถานะคำร้อง · ลบคำร้องที่ยังอยู่ในสถานะ PENDING · แก้ไขข้อมูลส่วนตัว |
| **ผู้ดูแลระบบ (Admin)** | ดูคำร้องทั้งหมด · อนุมัติหรือปฏิเสธคำร้อง · มอบหมายงานให้ช่าง · ตรวจสอบและยืนยันผลการซ่อม · จัดการข้อมูลผู้แจ้ง ผู้พักอาศัย และช่าง |
| **ช่าง (Technician)** | ดูรายการงานซ่อมประจำวันพร้อมตัวกรองสถานะ · อัปเดตสถานะงานเป็นสำเร็จหรือไม่สำเร็จพร้อมบันทึกหมายเหตุ · รับการแจ้งเตือนเมื่อได้รับมอบหมายงานใหม่ |

### สถานะของคำร้อง

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

## เทคโนโลยีที่ใช้

| ส่วน | เทคโนโลยี |
|---|---|
| ภาษา | Java 17 |
| Framework | Spring Boot 4.1 (Spring MVC, Spring Data JPA, Spring Security, Validation) |
| หน้าเว็บ | Thymeleaf, HTML, CSS |
| ฐานข้อมูล | PostgreSQL บน Supabase |
| การยืนยันตัวตน | Supabase Auth |
| Database Migration | Flyway |
| API Documentation | springdoc-openapi (Swagger UI) |
| Build Tool | Maven |
| Container / Deploy | Docker, Railway |

## สถาปัตยกรรมและการออกแบบ

ระบบใช้สถาปัตยกรรมแบบแบ่งชั้น (Layered Architecture) ร่วมกับ MVC

```
Controller (web / api)  →  Service  →  Repository  →  PostgreSQL (Supabase)
        ↓
Thymeleaf Templates
```

Design Pattern หลักที่ใช้

- **State Pattern** ควบคุมการเปลี่ยนสถานะของคำร้องให้เป็นไปตามกฎ
- **Command Pattern** ห่อหุ้มคำสั่งมอบหมายงานและยืนยันผลการซ่อมของผู้ดูแลระบบ
- **Observer Pattern** แจ้งเหตุการณ์เมื่อสถานะเปลี่ยนหรือมีการมอบหมายงาน เพื่อสร้างการแจ้งเตือน

รายละเอียดอยู่ใน [doc/design-patterns.md](doc/design-patterns.md) และการวิเคราะห์ตามหลัก SOLID อยู่ใน [doc/solid-analysis.md](doc/solid-analysis.md)

## โครงสร้างโปรเจกต์

```
Dormitory-Maintenance-Request-System/
├── doc/
│   ├── design-patterns.md
│   └── solid-analysis.md
└── dormitory/dormitory/
    ├── Dockerfile
    ├── docker-compose.yml
    ├── pom.xml
    └── src/main/
        ├── java/com/example/dormitory/
        │   ├── config/          # Security, Supabase, OpenAPI
        │   ├── controller/
        │   │   ├── web/         # Controller ของหน้าเว็บ (Thymeleaf)
        │   │   └── api/         # REST API
        │   ├── service/         # ตรรกะทางธุรกิจ (+ impl/)
        │   ├── repository/      # Spring Data JPA
        │   ├── domain/
        │   │   ├── entity/      # Entity ของฐานข้อมูล
        │   │   ├── enums/       # สถานะ ประเภทงานซ่อม บทบาท
        │   │   ├── state/       # State Pattern
        │   │   └── command/     # Command Pattern
        │   ├── event/           # Observer Pattern
        │   ├── dto/             # Request / Response DTO
        │   ├── exception/       # จัดการข้อผิดพลาด
        │   └── util/
        └── resources/
            ├── db/migration/    # Flyway migration
            ├── templates/       # admin/, reporter/, technician/
            └── static/css/
```

## การติดตั้งและรันบนเครื่อง

### สิ่งที่ต้องมี

- JDK 17 ขึ้นไป
- Maven 3.9 ขึ้นไป (หรือใช้ `mvnw` ที่มากับโปรเจกต์)
- โปรเจกต์ Supabase (ฐานข้อมูล PostgreSQL และ Auth)

### 1. Clone โปรเจกต์

```bash
git clone <!-- ใส่ลิงก์ repository -->
cd Dormitory-Maintenance-Request-System/dormitory/dormitory
```

### 2. ตั้งค่า Environment

สร้างไฟล์ `src/main/resources/application.properties` (ไฟล์นี้ไม่อยู่ใน repository เพราะมีคีย์ลับ) แล้วกำหนดค่าดังนี้

```properties
spring.datasource.url=jdbc:postgresql://<SUPABASE_DB_HOST>:5432/postgres
spring.datasource.username=<DB_USERNAME>
spring.datasource.password=<DB_PASSWORD>

supabase.url=https://<PROJECT_ID>.supabase.co
supabase.key=<SUPABASE_ANON_KEY>
```

> อย่า commit ไฟล์ที่มีรหัสผ่านหรือคีย์จริงขึ้น repository

### 3. รันแอปพลิเคชัน

```bash
./mvnw spring-boot:run        # macOS / Linux
mvnw.cmd spring-boot:run      # Windows
```

เปิดเว็บที่ http://localhost:8080 และดูเอกสาร API ที่ http://localhost:8080/swagger-ui/index.html

### รันด้วย Docker

```bash
docker compose up --build
```

## REST API

| Path | คำอธิบาย | สิทธิ์ |
|---|---|---|
| `/api/v1/repair-requests/**` | จัดการคำร้องแจ้งซ่อม | ผู้ใช้ที่เข้าสู่ระบบ |
| `/api/v1/reporters/**` | ข้อมูลและคำร้องของผู้แจ้ง | REPORTER |
| `/api/admin/reporters/**` | จัดการข้อมูลผู้แจ้ง | ผู้ใช้ที่เข้าสู่ระบบ |
| `/api/admin/residents/**` | จัดการข้อมูลผู้พักอาศัย | ผู้ใช้ที่เข้าสู่ระบบ |
| `/api/admin/technicians/**` | จัดการข้อมูลช่าง | ผู้ใช้ที่เข้าสู่ระบบ |

รายละเอียด endpoint ทั้งหมดดูได้จาก Swagger UI

## การ Deploy

ระบบ Deploy บน Railway โดย build จาก `Dockerfile` (multi-stage build ด้วย Maven และ JRE 17) และเชื่อมต่อฐานข้อมูล PostgreSQL บน Supabase ค่า Environment ตั้งไว้ใน Railway Variables

- URL: <!-- ใส่ลิงก์ deploy -->

## ผู้จัดทำ

| รหัสนักศึกษา | ชื่อ-นามสกุล | หน้าที่ |
|---|---|---|
| | | |
| | | |
| | | |