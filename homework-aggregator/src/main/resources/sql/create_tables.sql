CREATE TABLE IF NOT EXISTS public.users
(
    user_id   BIGSERIAL PRIMARY KEY,
    login     VARCHAR(50) UNIQUE NOT NULL,
    password  VARCHAR(255)        NOT NULL,
    activation_code varchar(255),
    email varchar(255),
    fio       VARCHAR(127),
    birthdate DATE,
    status    VARCHAR(50),
    role      VARCHAR(50),
    active    BOOLEAN
);

CREATE TABLE IF NOT EXISTS public.groups
(
    group_id   BIGSERIAL PRIMARY KEY,
    name_group VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS public.student
(
    student_id     BIGSERIAL PRIMARY KEY,
    user_id        BIGINT        NOT NULL,
    student_ticket BIGINT UNIQUE NOT NULL,
    group_id       BIGINT        NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    FOREIGN KEY (group_id) REFERENCES groups (group_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS public.discipline
(
    discipline_id   BIGSERIAL PRIMARY KEY,
    name_discipline VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS public.group_discipline
(
    group_discipline_id BIGSERIAL PRIMARY KEY,
    group_id            BIGINT NOT NULL,
    discipline_id       BIGINT NOT NULL,
    FOREIGN KEY (group_id) REFERENCES groups (group_id) ON DELETE CASCADE,
    FOREIGN KEY (discipline_id) REFERENCES discipline (discipline_id) ON DELETE CASCADE,
    UNIQUE (group_id, discipline_id)
);

CREATE TABLE IF NOT EXISTS public.teacher
(
    teacher_id BIGSERIAL PRIMARY KEY,
    user_id    BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS public.teacher_discipline
(
    teacher_discipline_id BIGSERIAL PRIMARY KEY,
    teacher_id            BIGINT,
    discipline_id         BIGINT,
    FOREIGN KEY (teacher_id) REFERENCES teacher (teacher_id) ON DELETE CASCADE,
    FOREIGN KEY (discipline_id) REFERENCES discipline (discipline_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS public.teacher_group
(
    teacher_group_id BIGSERIAL PRIMARY KEY,
    teacher_id       BIGINT,
    group_id         BIGINT,
    FOREIGN KEY (teacher_id) REFERENCES teacher (teacher_id) ON DELETE CASCADE,
    FOREIGN KEY (group_id) REFERENCES groups (group_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS public.status_task
(
    status_task_id BIGSERIAL PRIMARY KEY,
    status         VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS public.lesson_type
(
    lesson_type_id BIGSERIAL PRIMARY KEY,
    type           VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS public.pair
(
    pair_id BIGSERIAL PRIMARY KEY,
    start   TIME,
    finish  TIME
);

CREATE TABLE IF NOT EXISTS public.lessons
(
    lessons_id     BIGSERIAL PRIMARY KEY,
    date           DATE,
    pair_id        BIGINT NOT NULL,
    group_id       BIGINT NOT NULL,
    teacher_id     BIGINT NOT NULL,
    discipline_id  BIGINT NOT NULL,
    classroom      VARCHAR(50),
    lesson_type_id BIGINT,
    FOREIGN KEY (discipline_id) REFERENCES discipline (discipline_id) ON DELETE CASCADE,
    FOREIGN KEY (lesson_type_id) REFERENCES lesson_type (lesson_type_id) ON DELETE SET NULL,
    FOREIGN KEY (teacher_id) REFERENCES teacher (teacher_id) ON DELETE CASCADE,
    FOREIGN KEY (group_id) REFERENCES groups (group_id) ON DELETE CASCADE,
    FOREIGN KEY (pair_id) REFERENCES pair (pair_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS public.lesson_message
(
    lesson_message_id BIGSERIAL PRIMARY KEY,
    lessons_id        BIGINT  NOT NULL,
    text_message      VARCHAR(2048),
    file              VARCHAR(255),
    need_to_perform   BOOLEAN NOT NULL,
    deadline          TIMESTAMP,
    status_task_id    BIGINT,
    FOREIGN KEY (status_task_id) REFERENCES status_task (status_task_id) ON DELETE SET NULL,
    FOREIGN KEY (lessons_id) REFERENCES lessons (lessons_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS public.message
(
    message_id   BIGSERIAL PRIMARY KEY,
    from_id      BIGINT,
    to_id        BIGINT,
    time         TIMESTAMP     NOT NULL,
    text_message VARCHAR(2048) NOT NULL,
    file         VARCHAR(255),
    FOREIGN KEY (from_id) REFERENCES users (user_id) ON DELETE SET NULL,
    FOREIGN KEY (to_id) REFERENCES users (user_id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS public.task
(
    task_id           BIGSERIAL PRIMARY KEY,
    task              VARCHAR(2048) NOT NULL,
    teacher_id        BIGINT,
    discipline_id     BIGINT,
    lesson_message_id BIGINT,
    FOREIGN KEY (lesson_message_id) REFERENCES lesson_message (lesson_message_id) ON DELETE CASCADE,
    FOREIGN KEY (discipline_id) REFERENCES discipline (discipline_id) ON DELETE CASCADE,
    FOREIGN KEY (teacher_id) REFERENCES teacher (teacher_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS public.electronic_journal
(
    electronic_journal_id BIGSERIAL PRIMARY KEY,
    student_id            BIGINT,
    task_id               BIGINT,
    mark                  INTEGER,
    comment               VARCHAR(2048),
    FOREIGN KEY (student_id) REFERENCES student (student_id) ON DELETE CASCADE,
    FOREIGN KEY (task_id) REFERENCES task (task_id) ON DELETE CASCADE,
    CHECK (mark >= 0 AND mark <= 5)
);