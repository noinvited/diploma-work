-- Удаление старой таблицы сообщений
DROP TABLE IF EXISTS public.message;

-- Создание таблицы для сдачи заданий
CREATE TABLE IF NOT EXISTS public.submission
(
    submission_id    BIGSERIAL PRIMARY KEY,
    task_id         BIGINT NOT NULL,
    student_id      BIGINT NOT NULL,
    status_task_id  BIGINT,
    submission_date TIMESTAMP NOT NULL,
    last_update_date TIMESTAMP NOT NULL,
    files          VARCHAR(2048),
    FOREIGN KEY (task_id) REFERENCES task (task_id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES student (student_id) ON DELETE CASCADE,
    FOREIGN KEY (status_task_id) REFERENCES status_task (status_task_id) ON DELETE SET NULL
);

-- Создание таблицы для сообщений в системе сдачи заданий
CREATE TABLE IF NOT EXISTS public.submission_message
(
    message_id       BIGSERIAL PRIMARY KEY,
    submission_id    BIGINT NOT NULL,
    author_id        BIGINT,
    message_text     VARCHAR(2048) NOT NULL,
    files           VARCHAR(2048),
    created_at      TIMESTAMP NOT NULL,
    is_teacher_message BOOLEAN NOT NULL,
    FOREIGN KEY (submission_id) REFERENCES submission (submission_id) ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES users (user_id) ON DELETE SET NULL
); 