-- users

CREATE TABLE users (
    user_id     UUID         NOT NULL,
    first_name  VARCHAR(255),
    last_name   VARCHAR(255),
    password    VARCHAR(255),
    phone_no    VARCHAR(255),
    role        VARCHAR(255),
    username    VARCHAR(255),
    email       VARCHAR(255),
    CONSTRAINT users_pkey
        PRIMARY KEY (user_id),
    CONSTRAINT uk6dotkott2kjsp8vw4d0m25fb7
        UNIQUE (email),
    CONSTRAINT ukr43af9ap4edm43mmtq01oddj6
        UNIQUE (username)
);


-- building

CREATE TABLE building (
    building_no   INTEGER      NOT NULL,
    building_name VARCHAR(255),
    total_floor   INTEGER      NOT NULL,
    CONSTRAINT building_pkey
        PRIMARY KEY (building_no)
);


-- room 

CREATE TABLE room (
    room_no     INTEGER NOT NULL,
    building_no INTEGER,
    CONSTRAINT room_pkey
        PRIMARY KEY (room_no),
    CONSTRAINT fkd2urp82loslc8ntrtag9q03a7
        FOREIGN KEY (building_no) REFERENCES building (building_no)
);


-- resident 

CREATE TABLE resident (
    resident_id UUID         NOT NULL,
    first_name  VARCHAR(255),
    last_name   VARCHAR(255),
    phone_no    VARCHAR(255),
    room_no     INTEGER,
    CONSTRAINT resident_pkey
        PRIMARY KEY (resident_id),
    CONSTRAINT fkrvb001j5gu8jaxaqmii3s1qli
        FOREIGN KEY (room_no) REFERENCES room (room_no)
);


-- admin

CREATE TABLE admin (
    admin_id UUID NOT NULL,
    user_id  UUID NOT NULL,
    CONSTRAINT admin_pkey
        PRIMARY KEY (admin_id),
    CONSTRAINT ukhawikyhwwfvbnog5byokutpff
        UNIQUE (user_id),
    CONSTRAINT fkq7pdkck9je126wpd9ijw3uwml
        FOREIGN KEY (user_id) REFERENCES users (user_id)
);


-- technician 

CREATE TABLE technician (
    technician_id  UUID         NOT NULL,
    specialization VARCHAR(255),
    user_id        UUID         NOT NULL,
    CONSTRAINT technician_pkey
        PRIMARY KEY (technician_id),
    CONSTRAINT ukjcrt1trgnxmgib6u2p1fpqib2
        UNIQUE (user_id),
    CONSTRAINT fknxerfu3dpwim5li2bcfi3xsnr
        FOREIGN KEY (user_id) REFERENCES users (user_id)
);


-- reporter 

CREATE TABLE reporter (
    reporter_id UUID NOT NULL,
    resident_id UUID,
    user_id     UUID,
    CONSTRAINT reporter_pkey
        PRIMARY KEY (reporter_id),
    CONSTRAINT uk92ntmt05y7mhw2sdkuby3yx00
        UNIQUE (resident_id),
    CONSTRAINT ukd19jyy5dyce38d2xan1gygmrm
        UNIQUE (user_id),
    CONSTRAINT fk6g2t6mdn3q3qdxsqjj3s1pmvr
        FOREIGN KEY (user_id) REFERENCES users (user_id),
    CONSTRAINT fkrdc5oy2h1gjc5gj3g89radoli
        FOREIGN KEY (resident_id) REFERENCES resident (resident_id)
);


-- repair_request

CREATE TABLE repair_request (
    repair_request_id UUID         NOT NULL,
    description       TEXT         NOT NULL,
    end_date_time     TIMESTAMP,
    reporter_note     TEXT,
    repair_type       VARCHAR(255) NOT NULL,
    start_date_time   TIMESTAMP,
    status            VARCHAR(255) NOT NULL,
    admin_id          UUID,
    reporter_id       UUID         NOT NULL,
    room_no           INTEGER      NOT NULL,
    created_at        TIMESTAMP    NOT NULL,
    CONSTRAINT repair_request_pkey
        PRIMARY KEY (repair_request_id),
    CONSTRAINT fkcgnkvii1os3vluf983yis999l
        FOREIGN KEY (reporter_id) REFERENCES reporter (reporter_id),
    CONSTRAINT fki1ti2q2bcok3vx7vsnjgtkagk
        FOREIGN KEY (room_no) REFERENCES room (room_no),
    CONSTRAINT fkkxe2ochsmk34omi3jhirx5b1a
        FOREIGN KEY (admin_id) REFERENCES admin (admin_id)
);


-- repair_request_status_history 

CREATE TABLE repair_request_status_history (
    request_history_id UUID         NOT NULL,
    change_date        TIMESTAMP    NOT NULL,
    new_status         VARCHAR(255) NOT NULL,
    previous_status    VARCHAR(255),
    change_by          UUID         NOT NULL,
    repair_request_id  UUID         NOT NULL,
    CONSTRAINT repair_request_status_history_pkey
        PRIMARY KEY (request_history_id),
    CONSTRAINT fk4tt877ywm625s5qos0shf7s5f
        FOREIGN KEY (change_by) REFERENCES users (user_id),
    CONSTRAINT fkieelo8dryuy2y5oib1vxub3qj
        FOREIGN KEY (repair_request_id) REFERENCES repair_request (repair_request_id)
);


-- repair_assignment

CREATE TABLE repair_assignment (
    assignment_id     UUID         NOT NULL,
    assign_date       TIMESTAMP    NOT NULL,
    job_status        VARCHAR(255) NOT NULL,
    note              TEXT,
    admin_id          UUID         NOT NULL,
    repair_request_id UUID         NOT NULL,
    technician_id     UUID         NOT NULL,
    technician_note   VARCHAR(255),
    admin_note        TEXT,
    CONSTRAINT repair_assignment_pkey
        PRIMARY KEY (assignment_id),
    CONSTRAINT fka9jq5f9d8pdiyoudtacsm9hji
        FOREIGN KEY (technician_id) REFERENCES technician (technician_id),
    CONSTRAINT fkfjffnioqfx5ycito9l5s4dx7l
        FOREIGN KEY (admin_id) REFERENCES admin (admin_id),
    CONSTRAINT fkk4tiix9nayphlp1b8pguwsumu
        FOREIGN KEY (repair_request_id) REFERENCES repair_request (repair_request_id)
);


-- repair_assignment_status_history

CREATE TABLE repair_assignment_status_history (
    history_id      UUID         NOT NULL,
    change_date     TIMESTAMP    NOT NULL,
    new_status      VARCHAR(255) NOT NULL,
    previous_status VARCHAR(255),
    assignment_id   UUID         NOT NULL,
    change_by       UUID         NOT NULL,
    CONSTRAINT repair_assignment_status_history_pkey
        PRIMARY KEY (history_id),
    CONSTRAINT fksbytm41sdla1y4nu2gvxkehs8
        FOREIGN KEY (assignment_id) REFERENCES repair_assignment (assignment_id),
    CONSTRAINT fktkrjw943xsk84v2mionometys
        FOREIGN KEY (change_by) REFERENCES users (user_id)
);


-- notification

CREATE TABLE notification (
    notification_id UUID         NOT NULL,
    created_at      TIMESTAMP    NOT NULL,
    event_type      VARCHAR(255) NOT NULL,
    message         VARCHAR(255) NOT NULL,
    is_read         BOOLEAN      NOT NULL,
    reference_id    UUID,
    title           VARCHAR(255) NOT NULL,
    recipient_id    UUID         NOT NULL,
    CONSTRAINT notification_pkey
        PRIMARY KEY (notification_id),
    CONSTRAINT fkfcyn9rsga73dqnorl7owfyl4a
        FOREIGN KEY (recipient_id) REFERENCES users (user_id),
    CONSTRAINT notification_event_type_check
        CHECK (event_type IN (
            'NEW_REPAIR_REQUEST',
            'REPAIR_COMPLETED',
            'REPAIR_NOT_COMPLETED',
            'NEW_REPAIR_ASSIGNMENT',
            'ASSIGNMENT_APPROVED'
        ))
);