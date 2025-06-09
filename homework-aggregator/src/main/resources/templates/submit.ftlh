<#import "parts/commonTmpl.ftlh" as c>
<#include "parts/securityHlpr.ftlh">

<@c.page>
    <#if errorMessage??>
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            ${errorMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </#if>
    
    <#if successMessage??>
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            ${successMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </#if>

    <div class="mb-4">
        <a class="btn btn-outline-primary" href="${returnUrl!'/studentTasks'}">
            Вернуться назад
        </a>
    </div>

    <div class="row">
        <!-- Информация о задании -->
        <div class="col-md-12 mb-4">
            <div class="card">
                <div class="card-header">
                    <h5 class="card-title mb-0">Информация о задании</h5>
                </div>
                <div class="card-body">
                    <h6 class="mb-3">${task.discipline.nameDiscipline}</h6>                    
                    <#if isTeacher>
                        <p class="mb-2">
                            <strong>Студент:</strong> ${student.user.fio!student.user.username}
                        </p>
                    <#else>
                        <p class="mb-2">
                            <strong>Преподаватель:</strong> ${task.teacher.user.fio!task.teacher.user.username}
                        </p>
                    </#if>
                    <p class="mb-2">
                        <strong>Дата занятия:</strong> ${task.lessonMessage.lessons.date?replace("Z", "")?replace("T", " ")?datetime("yyyy-MM-dd")?string('dd.MM.yyyy')}, пара №${task.lessonMessage.lessons.pair.id}
                    </p>
                    <#if task.lessonMessage.deadline??>
                        <p class="mb-2">
                            <strong>Срок сдачи:</strong> ${task.lessonMessage.getFormattedDeadline()!'Не указан'}
                        </p>
                    </#if>
                    <div class="mt-3">
                        <strong>Текст задания:</strong>
                        <p class="mt-2">${task.task}</p>
                    </div>
                    <#if task.lessonMessage.file?? && task.lessonMessage.file?has_content>
                        <div class="mt-3">
                            <strong>Прикрепленные файлы:</strong><br>
                            <#list task.lessonMessage.file?split(";") as fileName>
                                <a href="/files/${fileName}" class="btn btn-sm btn-outline-primary me-2 mt-2">
                                    <i class="bi bi-download"></i> ${fileName?keep_after_last("/")}
                                </a>
                            </#list>
                        </div>
                    </#if>
                    <#if submission??>
                        <div class="mt-3 pt-3 border-top">
                            <strong>Статус:</strong>
                            <span class="badge bg-secondary ms-2">${submission.statusTask.status}</span>
                            <#if isTeacher && journal??>
                                <strong class="ms-3">Оценка:</strong>
                                <span class="badge bg-success ms-2">${journal.mark}</span>
                            </#if>
                        </div>
                    </#if>
                </div>
            </div>
        </div>

        <!-- История диалога -->
        <div class="col-md-12 mb-4">
            <div class="card">
                <div class="card-header">
                    <h5 class="card-title mb-0">История диалога</h5>
                </div>
                <div class="card-body">
                    <div class="submission-messages">
                        <#if messages?? && messages?has_content>
                            <#list messages as message>
                                <div class="message mb-3 p-3 rounded ${message.isTeacherMessage?string('bg-light border', 'bg-primary bg-opacity-10')}">
                                    <div class="d-flex justify-content-between align-items-start mb-2">
                                        <strong>${message.isTeacherMessage?string('Преподаватель', student.user.fio!student.user.username)}</strong>
                                        <small class="text-muted">${message.createdAt?replace("Z", "")?replace("T", " ")?datetime("yyyy-MM-dd HH:mm:ss")?string("dd.MM.yyyy HH:mm")}</small>
                                    </div>
                                    <p class="mb-2">${message.messageText}</p>
                                    <#if message.files?? && message.files?has_content>
                                        <div class="mt-2">
                                            <#list message.files?split(";") as fileName>
                                                <a href="/files/${fileName}" class="btn btn-sm btn-outline-primary me-2">
                                                    <i class="bi bi-download"></i> ${fileName?keep_after_last("/")}
                                                </a>
                                            </#list>
                                        </div>
                                    </#if>
                                </div>
                            </#list>
                        <#else>
                            <p class="text-muted">История сообщений пуста</p>
                        </#if>
                    </div>
                </div>
            </div>
        </div>

        <!-- Форма отправки -->
        <div class="col-md-12">
            <div class="card">
                <div class="card-header">
                    <h5 class="card-title mb-0"><#if isTeacher>Проверка задания<#else>Отправка решения</#if></h5>
                </div>
                <div class="card-body">
                    <form action="/submit/${task.lessonMessage.id}${returnUrl???then('?returnUrl=' + returnUrl, '')}" method="post" enctype="multipart/form-data">
                        <div class="mb-3">
                            <label for="messageText" class="form-label">Сообщение</label>
                            <textarea class="form-control" id="messageText" name="messageText" rows="3" required></textarea>
                        </div>
                        <div class="mb-3">
                            <label for="files" class="form-label">Прикрепить файлы</label>
                            <input type="file" class="form-control" id="files" name="files" multiple>
                        </div>
                        <#if isTeacher>
                            <div class="mb-3">
                                <label for="mark" class="form-label">Оценка</label>
                                <input type="number" class="form-control" id="mark" name="mark" min="2" max="5" step="1">
                            </div>
                            <div class="d-flex justify-content-between">
                                <button type="submit" name="action" value="comment" class="btn btn-primary" onclick="return validateTeacherComment()">
                                    <i class="bi bi-send"></i> Отправить комментарий
                                </button>
                                <button type="submit" name="action" value="grade" class="btn btn-success" onclick="return validateTeacherGrade()">
                                    <i class="bi bi-check2-circle"></i> Выставить оценку
                                </button>
                            </div>
                        <#else>
                            <div class="d-flex justify-content-between">
                                <button type="submit" name="action" value="save" class="btn btn-primary" onclick="return validateStudentSubmission()">
                                    <i class="bi bi-send"></i> Отправить сообщение
                                </button>
                                <button type="submit" name="action" value="submit" class="btn btn-success" onclick="return validateStudentSubmission()">
                                    <i class="bi bi-check2-circle"></i> Отправить на проверку
                                </button>
                            </div>
                        </#if>
                        <#if returnUrl??>
                            <input type="hidden" name="returnUrl" value="${returnUrl}">
                        </#if>
                        <#if studentId??>
                            <input type="hidden" name="studentId" value="${studentId}">
                        </#if>
                    </form>
                </div>
            </div>
        </div>
    </div>
    <div class="mb-5"></div>

    <script>
        function validateStudentSubmission() {
            const messageText = document.getElementById('messageText').value.trim();
            const files = document.getElementById('files').files;
            
            if (messageText === '' && files.length === 0) {
                alert('Необходимо заполнить текст сообщения или прикрепить файл');
                return false;
            }
            return true;
        }

        function validateTeacherComment() {
            const messageText = document.getElementById('messageText').value.trim();
            const files = document.getElementById('files').files;
            
            if (messageText === '' && files.length === 0) {
                alert('Необходимо заполнить текст комментария или прикрепить файл');
                return false;
            }
            return true;
        }

        function validateTeacherGrade() {
            const mark = document.getElementById('mark').value;
            
            if (!mark) {
                alert('Необходимо выставить оценку');
                return false;
            }
            return true;
        }
    </script>
</@c.page>