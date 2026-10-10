-- =========================================================
-- 1. USERS
-- =========================================================

INSERT INTO users (
    user_id,
    first_name,
    last_name,
    password,
    phone_no,
    role,
    username,
    email
) VALUES

-- Admin
(
    '11111111-1111-1111-1111-111111111111',
    'สมชาย',
    'ใจดี',
    'admin123',
    '0812345678',
    'ADMIN',
    'admin01',
    'admin01@example.com'
),

-- Reporter 1
(
    '22222222-2222-2222-2222-222222222222',
    'กิตติ',
    'พัฒนากร',
    'reporter123',
    '0811111111',
    'REPORTER',
    'reporter01',
    'reporter01@example.com'
),

-- Reporter 2
(
    '33333333-3333-3333-3333-333333333333',
    'ณัฐชา',
    'ศรีสุข',
    'reporter123',
    '0822222222',
    'REPORTER',
    'reporter02',
    'reporter02@example.com'
),

-- Technician 1
(
    '44444444-4444-4444-4444-444444444444',
    'วิชัย',
    'ช่างดี',
    'technician123',
    '0833333333',
    'TECHNICIAN',
    'technician01',
    'technician01@example.com'
),

-- Technician 2
(
    '55555555-5555-5555-5555-555555555555',
    'ประสิทธิ์',
    'ช่างเก่ง',
    'technician123',
    '0844444444',
    'TECHNICIAN',
    'technician02',
    'technician02@example.com'
);


-- =========================================================
-- 2. BUILDING
-- =========================================================

INSERT INTO building (
    building_no,
    building_name,
    total_floor
) VALUES
(
    3,
    'อาคารหอพักชาย 3',
    5
),
(
    4,
    'อาคารหอพักหญิง 4',
    5
);


-- =========================================================
-- 3. ROOM
-- =========================================================

INSERT INTO room (
    room_no,
    building_no
) VALUES
(101, 3),
(102, 3),
(103, 3),
(201, 4),
(202, 4),
(203, 4);


-- =========================================================
-- 4. RESIDENT
-- =========================================================

INSERT INTO resident (
    resident_id,
    first_name,
    last_name,
    phone_no,
    room_no
) VALUES

(
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    'กิตติ',
    'พัฒนากร',
    '0811111111',
    101
),

(
    'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
    'ณัฐชา',
    'ศรีสุข',
    '0822222222',
    201
);


-- =========================================================
-- 5. ADMIN
-- =========================================================

INSERT INTO admin (
    admin_id,
    user_id
) VALUES
(
    '66666666-6666-6666-6666-666666666666',
    '11111111-1111-1111-1111-111111111111'
);


-- =========================================================
-- 6. TECHNICIAN
-- =========================================================

INSERT INTO technician (
    technician_id,
    specialization,
    user_id
) VALUES

(
    '77777777-7777-7777-7777-777777777777',
    'งานไฟฟ้า',
    '44444444-4444-4444-4444-444444444444'
),

(
    '88888888-8888-8888-8888-888888888888',
    'งานประปา',
    '55555555-5555-5555-5555-555555555555'
);


-- =========================================================
-- 7. REPORTER
-- =========================================================

INSERT INTO reporter (
    reporter_id,
    resident_id,
    user_id
) VALUES

(
    '99999999-9999-9999-9999-999999999999',
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    '22222222-2222-2222-2222-222222222222'
),

(
    'aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee',
    'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
    '33333333-3333-3333-3333-333333333333'
);


-- =========================================================
-- 8. REPAIR REQUEST
-- =========================================================

-- Request 1
-- APPROVED → มีการมอบหมายงาน
INSERT INTO repair_request (
    repair_request_id,
    description,
    end_date_time,
    reporter_note,
    repair_type,
    start_date_time,
    status,
    admin_id,
    reporter_id,
    room_no,
    created_at
) VALUES
(
    'aaaaaaaa-1111-1111-1111-111111111111',
    'ไฟห้องน้ำไม่ติด',
    '2026-10-10 18:00:00',
    'สะดวกให้เข้าซ่อมช่วงเย็น',
    'ELECTRICAL',
    '2026-10-09 17:00:00',
    'APPROVED',
    '66666666-6666-6666-6666-666666666666',
    '99999999-9999-9999-9999-999999999999',
    101,
    '2026-10-08 09:00:00'
);


-- Request 2
-- IN_PROGRESS → มี Assignment
INSERT INTO repair_request (
    repair_request_id,
    description,
    end_date_time,
    reporter_note,
    repair_type,
    start_date_time,
    status,
    admin_id,
    reporter_id,
    room_no,
    created_at
) VALUES
(
    'bbbbbbbb-2222-2222-2222-222222222222',
    'ก๊อกน้ำในห้องน้ำรั่ว',
    '2026-10-09 17:00:00',
    'หากเข้าซ่อมกรุณาแจ้งก่อน',
    'PLUMBING',
    '2026-10-09 10:00:00',
    'IN_PROGRESS',
    '66666666-6666-6666-6666-666666666666',
    'aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee',
    201,
    '2026-10-08 10:00:00'
);


-- Request 3
-- COMPLETED
INSERT INTO repair_request (
    repair_request_id,
    description,
    end_date_time,
    reporter_note,
    repair_type,
    start_date_time,
    status,
    admin_id,
    reporter_id,
    room_no,
    created_at
) VALUES
(
    'cccccccc-3333-3333-3333-333333333333',
    'หลอดไฟบริเวณโต๊ะอ่านหนังสือเสีย',
    '2026-10-07 18:00:00',
    'สามารถเข้าซ่อมได้ตลอดช่วงเย็น',
    'ELECTRICAL',
    '2026-10-07 16:00:00',
    'COMPLETED',
    '66666666-6666-6666-6666-666666666666',
    '99999999-9999-9999-9999-999999999999',
    101,
    '2026-10-06 14:00:00'
);


-- Request 4
-- REJECTED
INSERT INTO repair_request (
    repair_request_id,
    description,
    end_date_time,
    reporter_note,
    repair_type,
    start_date_time,
    status,
    admin_id,
    reporter_id,
    room_no,
    created_at
) VALUES
(
    'dddddddd-4444-4444-4444-444444444444',
    'ต้องการติดตั้งอุปกรณ์เพิ่มเติมภายในห้อง',
    '2026-10-12 17:00:00',
    NULL,
    'OTHER',
    '2026-10-12 13:00:00',
    'REJECTED',
    '66666666-6666-6666-6666-666666666666',
    'aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee',
    201,
    '2026-10-05 11:00:00'
);


-- Request 5
-- PENDING
-- ไม่มี Assignment → สามารถทดสอบ Delete ได้
INSERT INTO repair_request (
    repair_request_id,
    description,
    end_date_time,
    reporter_note,
    repair_type,
    start_date_time,
    status,
    admin_id,
    reporter_id,
    room_no,
    created_at
) VALUES
(
    'eeeeeeee-5555-5555-5555-555555555555',
    'แอร์ภายในห้องไม่เย็น',
    '2026-10-11 17:00:00',
    'สะดวกให้เข้าตรวจสอบช่วงเย็น',
    'AIR_CONDITIONER',
    '2026-10-11 15:00:00',
    'PENDING',
    NULL,
    '99999999-9999-9999-9999-999999999999',
    101,
    '2026-10-08 20:00:00'
);


-- =========================================================
-- 9. REPAIR REQUEST STATUS HISTORY
-- =========================================================

-- Request 1: PENDING → APPROVED
INSERT INTO repair_request_status_history (
    request_history_id,
    change_date,
    new_status,
    previous_status,
    change_by,
    repair_request_id
) VALUES
(
    '11111111-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    '2026-10-08 09:30:00',
    'APPROVED',
    'PENDING',
    '11111111-1111-1111-1111-111111111111',
    'aaaaaaaa-1111-1111-1111-111111111111'
);


-- Request 2: PENDING → APPROVED
INSERT INTO repair_request_status_history (
    request_history_id,
    change_date,
    new_status,
    previous_status,
    change_by,
    repair_request_id
) VALUES
(
    '22222222-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
    '2026-10-08 10:30:00',
    'APPROVED',
    'PENDING',
    '11111111-1111-1111-1111-111111111111',
    'bbbbbbbb-2222-2222-2222-222222222222'
);


-- Request 2: APPROVED → IN_PROGRESS
INSERT INTO repair_request_status_history (
    request_history_id,
    change_date,
    new_status,
    previous_status,
    change_by,
    repair_request_id
) VALUES
(
    '33333333-cccc-cccc-cccc-cccccccccccc',
    '2026-10-08 11:00:00',
    'IN_PROGRESS',
    'APPROVED',
    '11111111-1111-1111-1111-111111111111',
    'bbbbbbbb-2222-2222-2222-222222222222'
);


-- Request 3: PENDING → APPROVED
INSERT INTO repair_request_status_history (
    request_history_id,
    change_date,
    new_status,
    previous_status,
    change_by,
    repair_request_id
) VALUES
(
    '44444444-dddd-dddd-dddd-dddddddddddd',
    '2026-10-06 15:00:00',
    'APPROVED',
    'PENDING',
    '11111111-1111-1111-1111-111111111111',
    'cccccccc-3333-3333-3333-333333333333'
);


-- Request 3: APPROVED → IN_PROGRESS
INSERT INTO repair_request_status_history (
    request_history_id,
    change_date,
    new_status,
    previous_status,
    change_by,
    repair_request_id
) VALUES
(
    '55555555-eeee-eeee-eeee-eeeeeeeeeeee',
    '2026-10-07 10:00:00',
    'IN_PROGRESS',
    'APPROVED',
    '11111111-1111-1111-1111-111111111111',
    'cccccccc-3333-3333-3333-333333333333'
);


-- Request 3: IN_PROGRESS → COMPLETED
INSERT INTO repair_request_status_history (
    request_history_id,
    change_date,
    new_status,
    previous_status,
    change_by,
    repair_request_id
) VALUES
(
    '66666666-ffff-ffff-ffff-ffffffffffff',
    '2026-10-07 18:30:00',
    'COMPLETED',
    'IN_PROGRESS',
    '11111111-1111-1111-1111-111111111111',
    'cccccccc-3333-3333-3333-333333333333'
);


-- Request 4: PENDING → REJECTED
INSERT INTO repair_request_status_history (
    request_history_id,
    change_date,
    new_status,
    previous_status,
    change_by,
    repair_request_id
) VALUES
(
    '77777777-aaaa-bbbb-cccc-dddddddddddd',
    '2026-10-05 11:30:00',
    'REJECTED',
    'PENDING',
    '11111111-1111-1111-1111-111111111111',
    'dddddddd-4444-4444-4444-444444444444'
);


-- =========================================================
-- 10. REPAIR ASSIGNMENT
-- =========================================================

-- Assignment for Request 1
INSERT INTO repair_assignment (
    assignment_id,
    assign_date,
    job_status,
    note,
    admin_id,
    repair_request_id,
    technician_id,
    technician_note,
    admin_note
) VALUES
(
    'aaaa1111-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    '2026-10-08 10:00:00',
    'ASSIGNED',
    'ตรวจสอบระบบไฟและเปลี่ยนหลอดไฟ',
    '66666666-6666-6666-6666-666666666666',
    'aaaaaaaa-1111-1111-1111-111111111111',
    '77777777-7777-7777-7777-777777777777',
    NULL,
    'กรุณาดำเนินการภายในเวลาที่กำหนด'
);


-- Assignment for Request 2
INSERT INTO repair_assignment (
    assignment_id,
    assign_date,
    job_status,
    note,
    admin_id,
    repair_request_id,
    technician_id,
    technician_note,
    admin_note
) VALUES
(
    'bbbb2222-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
    '2026-10-08 11:00:00',
    'IN_PROGRESS',
    'ตรวจสอบก๊อกน้ำและระบบท่อน้ำ',
    '66666666-6666-6666-6666-666666666666',
    'bbbbbbbb-2222-2222-2222-222222222222',
    '88888888-8888-8888-8888-888888888888',
    'พบรอยรั่วบริเวณข้อต่อ',
    'ให้ดำเนินการซ่อมและทดสอบระบบหลังซ่อม'
);


-- Assignment for Request 3
INSERT INTO repair_assignment (
    assignment_id,
    assign_date,
    job_status,
    note,
    admin_id,
    repair_request_id,
    technician_id,
    technician_note,
    admin_note
) VALUES
(
    'cccc3333-cccc-cccc-cccc-cccccccccccc',
    '2026-10-07 09:00:00',
    'COMPLETED',
    'เปลี่ยนหลอดไฟใหม่',
    '66666666-6666-6666-6666-666666666666',
    'cccccccc-3333-3333-3333-333333333333',
    '77777777-7777-7777-7777-777777777777',
    'เปลี่ยนหลอดไฟเรียบร้อยแล้ว',
    'รอผู้แจ้งยืนยันผลการซ่อม'
);


-- =========================================================
-- 11. REPAIR ASSIGNMENT STATUS HISTORY
-- =========================================================

-- Assignment 1: initial ASSIGNED
INSERT INTO repair_assignment_status_history (
    history_id,
    change_date,
    new_status,
    previous_status,
    assignment_id,
    change_by
) VALUES
(
    'aaaa4444-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    '2026-10-08 10:00:00',
    'ASSIGNED',
    NULL,
    'aaaa1111-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    '11111111-1111-1111-1111-111111111111'
);


-- Assignment 2: ASSIGNED → IN_PROGRESS
INSERT INTO repair_assignment_status_history (
    history_id,
    change_date,
    new_status,
    previous_status,
    assignment_id,
    change_by
) VALUES
(
    'bbbb5555-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
    '2026-10-08 12:00:00',
    'IN_PROGRESS',
    'ASSIGNED',
    'bbbb2222-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
    '55555555-5555-5555-5555-555555555555'
);


-- Assignment 3: ASSIGNED → IN_PROGRESS
INSERT INTO repair_assignment_status_history (
    history_id,
    change_date,
    new_status,
    previous_status,
    assignment_id,
    change_by
) VALUES
(
    'cccc6666-cccc-cccc-cccc-cccccccccccc',
    '2026-10-07 10:00:00',
    'IN_PROGRESS',
    'ASSIGNED',
    'cccc3333-cccc-cccc-cccc-cccccccccccc',
    '44444444-4444-4444-4444-444444444444'
);


-- Assignment 3: IN_PROGRESS → COMPLETED
INSERT INTO repair_assignment_status_history (
    history_id,
    change_date,
    new_status,
    previous_status,
    assignment_id,
    change_by
) VALUES
(
    'dddd7777-dddd-dddd-dddd-dddddddddddd',
    '2026-10-07 17:30:00',
    'COMPLETED',
    'IN_PROGRESS',
    'cccc3333-cccc-cccc-cccc-cccccccccccc',
    '44444444-4444-4444-4444-444444444444'
);

