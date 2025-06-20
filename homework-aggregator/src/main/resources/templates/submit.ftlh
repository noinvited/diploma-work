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
                            <#if journal?? && submission.statusTask.id == 5>
                                <strong class="ms-3">Оценка:</strong>
                                <span class="badge ${(journal.mark?string == "2")?then('bg-danger', (journal.mark?string == "3")?then('bg-warning', 'bg-success'))} ms-2">${journal.mark}</span>
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
                                <#assign isCurrentUser = (user.id == message.author.id)>
                                <div class="message mb-3 d-flex ${isCurrentUser?string('justify-content-end', 'justify-content-start')}">
                                    <div class="message-content p-3 rounded ${isCurrentUser?string('bg-primary bg-opacity-25', 'bg-light')} mw-75">
                                        <div class="d-flex justify-content-between align-items-start mb-2">
                                            <strong class="text-dark">${message.author.fio!message.author.username}</strong>
                                            <small class="text-muted ms-3">
                                                ${message.createdAt?replace("Z", "")?replace("T", " ")?datetime("yyyy-MM-dd HH:mm:ss")?string("dd.MM.yyyy HH:mm")}
                                            </small>
                                        </div>
                                        <p class="mb-2 ${isCurrentUser?string('text-dark', '')}">${message.messageText}</p>
                                        <#if message.files?? && message.files?has_content>
                                            <div class="mt-2">
                                                <#list message.files?split(";") as fileName>
                                                    <a href="/files/${fileName}" class="btn btn-sm ${isCurrentUser?string('btn-outline-primary', 'btn-outline-secondary')} me-2">
                                                        <i class="bi bi-download"></i> ${fileName?keep_after_last("/")}
                                                    </a>
                                                </#list>
                                            </div>
                                        </#if>
                                    </div>
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
                    <form action="/submit/${task.lessonMessage.id}${returnUrl???then('?returnUrl=' + returnUrl?url, '')}" method="post" enctype="multipart/form-data">
                        <div class="mb-3">
                            <label for="messageText" class="form-label">Сообщение</label>
                            <textarea class="form-control" id="messageText" name="messageText" rows="3"></textarea>
                        </div>
                        <div class="mb-3">
                            <label for="files" class="form-label">Прикрепить файлы</label>
                            <input type="file" class="form-control" id="files" name="files" multiple>
                        </div>
                        <#if isTeacher>
                            <div class="mb-3">
                                <div class="form-check mb-3">
                                    <input type="checkbox" class="form-check-input" id="needsRevision" name="needsRevision" onchange="toggleMarkInput()">
                                    <label class="form-check-label" for="needsRevision">Требуются доработки</label>
                                </div>
                                <div id="markInputGroup">
                                    <label for="mark" class="form-label">Оценка</label>
                                    <input type="number" class="form-control" id="mark" name="mark" min="2" max="5" step="1">
                                </div>
                            </div>
                            <div class="d-flex justify-content-between">
                                <button type="submit" name="action" value="comment" class="btn btn-primary" onclick="return validateTeacherMessage()">
                                    <i class="bi bi-send"></i> Отправить сообщение
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
                                <#if !(submission?? && submission.statusTask.id == 5 && journal??)>
                                    <button type="submit" name="action" value="submit" class="btn btn-success" onclick="return validateStudentSubmission()">
                                        <i class="bi bi-check2-circle"></i> Отправить на проверку
                                    </button>
                                </#if>
                            </div>
                        </#if>
                        <#if returnUrl??>
                            <input type="hidden" name="returnUrl" value="${returnUrl?url}">
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

        function validateTeacherMessage() {
            const messageText = document.getElementById('messageText').value.trim();
            const files = document.getElementById('files').files;
            const needsRevision = document.getElementById('needsRevision').checked;
            const mark = document.getElementById('mark').value;
            
            if (mark) {
                alert('При отправке сообщения нельзя выставлять оценку');
                return false;
            }
            
            if (!needsRevision && messageText === '' && files.length === 0) {
                alert('Необходимо заполнить текст сообщения или прикрепить файл');
                return false;
            }
            if (needsRevision && messageText === '') {
                alert('При отправке на доработку необходимо указать комментарий');
                return false;
            }
            return true;
        }

        function validateTeacherGrade() {
            const needsRevision = document.getElementById('needsRevision').checked;
            if (needsRevision) {
                alert('Нельзя выставить оценку, если требуются доработки');
                return false;
            }
            
            const mark = document.getElementById('mark').value;
            if (!mark) {
                alert('Необходимо выставить оценку');
                return false;
            }
            return true;
        }

        function toggleMarkInput() {
            const needsRevision = document.getElementById('needsRevision').checked;
            const markInputGroup = document.getElementById('markInputGroup');
            const mark = document.getElementById('mark');
            
            if (needsRevision) {
                markInputGroup.style.display = 'none';
                mark.value = '';
            } else {
                markInputGroup.style.display = 'block';
            }
        }
    </script>
</@c.page>