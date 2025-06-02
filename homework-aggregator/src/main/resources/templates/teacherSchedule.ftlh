<#import "parts/commonTmpl.ftlh" as c>
<#include "parts/securityHlpr.ftlh">

<@c.page>
    <#if isStudent == 0>
        <h4>Для дальнейшей работы заполните данные о профиле и дождитесь получения статуса от администратора сайта.</h4>
    <#else>
        <#if successMessage??>
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                ${successMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </#if>
        <#if errorMessage??>
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                ${errorMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </#if>

        <div class="card">
            <div class="card-header">
                <div class="d-flex justify-content-between align-items-center">
                    <h5 class="card-title mb-0">Расписание преподавателя</h5>
                    <div class="btn-group">
                        <a href="/teacherSchedule?weekShift=${weekShift - 1}" class="btn btn-outline-primary">
                            <i class="bi bi-chevron-left"></i> Предыдущая неделя
                        </a>
                        <a href="/teacherSchedule?weekShift=0" class="btn btn-outline-primary <#if weekShift == 0>active</#if>">
                            Текущая неделя
                        </a>
                        <a href="/teacherSchedule?weekShift=${weekShift + 1}" class="btn btn-outline-primary">
                            Следующая неделя <i class="bi bi-chevron-right"></i>
                        </a>
                    </div>
                </div>
            </div>
            <div class="card-body">
                <div class="table-responsive">
                    <table class="table table-bordered">
                        <thead>
                        <tr>
                            <th class="text-center">Время</th>
                            <#list days as day>
                                <th class="text-center">${day}</th>
                            </#list>
                        </tr>
                        </thead>
                        <tbody>
                        <#list pairTimes as pair>
                            <tr>
                                <td class="align-middle">
                                    ${pair.start?replace("Z", "")?replace("T", " ")?datetime("yyyy-MM-dd HH:mm:ss")?string("HH:mm")} -
                                    ${pair.finish?replace("Z", "")?replace("T", " ")?datetime("yyyy-MM-dd HH:mm:ss")?string("HH:mm")}
                                </td>
                                <#list dates as date>
                                    <td class="align-middle position-relative" style="min-width: 200px;">
                                        <#assign lessonKey = date?replace("Z", "")?replace("T", " ")?datetime("yyyy-MM-dd")?string('yyyy-MM-dd') + "_" + pair.id>
                                        <#if lessons[lessonKey]??>
                                            <#assign lesson = lessons[lessonKey]>
                                            <div class="card">
                                                <div class="card-body p-2">
                                                    <h6 class="card-title mb-1">${lesson.discipline.nameDiscipline}</h6>
                                                    <p class="card-text mb-1">
                                                        <small>
                                                            <i class="bi bi-people"></i> Группа: ${lesson.group.nameGroup}<br>
                                                            <i class="bi bi-geo-alt"></i> ${lesson.classroom!''}<br>
                                                            <#if lesson.lessonType??><i class="bi bi-journal-text"></i> ${lesson.lessonType.type}</#if>
                                                        </small>
                                                    </p>
                                                    <#if lessonMessages[lessonKey]??>
                                                        <button type="button" class="btn btn-sm btn-outline-primary mt-1" 
                                                                data-bs-toggle="modal" 
                                                                data-bs-target="#viewMessageModal"
                                                                data-lesson-id="${lesson.id}"
                                                                data-lesson-key="${lessonKey}"
                                                                data-discipline="${lesson.discipline.nameDiscipline}"
                                                                data-group="${lesson.group.nameGroup}"
                                                                data-date="${date?replace("Z", "")?replace("T", " ")?datetime("yyyy-MM-dd")?string('dd.MM.yyyy')}">
                                                            <i class="bi bi-eye"></i> Показать сообщение
                                                        </button>
                                                    <#else>
                                                        <button type="button" class="btn btn-sm btn-primary mt-1" 
                                                                data-bs-toggle="modal" 
                                                                data-bs-target="#messageModal"
                                                                data-lesson-id="${lesson.id}"
                                                                data-discipline="${lesson.discipline.nameDiscipline}"
                                                                data-group="${lesson.group.nameGroup}"
                                                                data-date="${date?replace("Z", "")?replace("T", " ")?datetime("yyyy-MM-dd")?string('dd.MM.yyyy')}">
                                                            <i class="bi bi-chat-dots"></i> Добавить сообщение
                                                        </button>
                                                    </#if>
                                                </div>
                                            </div>
                                        </#if>
                                    </td>
                                </#list>
                            </tr>
                        </#list>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <!-- Модальное окно для добавления сообщений -->
        <div class="modal fade" id="messageModal" tabindex="-1" aria-labelledby="messageModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <form id="lessonMessageForm" action="/addLessonMessage" method="POST" enctype="multipart/form-data">
                        <div class="modal-header">
                            <h5 class="modal-title" id="messageModalLabel">Добавить сообщение к занятию</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <input type="hidden" id="lessonId" name="lessonId">
                            <input type="hidden" id="weekShift" name="weekShift" value="${weekShift}">
                            <div class="mb-3">
                                <label class="form-label">Дисциплина: <span id="modalDiscipline"></span></label><br>
                                <label class="form-label">Группа: <span id="modalGroup"></span></label><br>
                                <label class="form-label">Дата: <span id="modalDate"></span></label>
                            </div>
                            <div class="mb-3">
                                <label for="messageText" class="form-label">Текст сообщения</label>
                                <textarea class="form-control" id="messageText" name="textMessage" rows="3" maxlength="2048" required></textarea>
                            </div>
                            <div class="mb-3">
                                <label for="file" class="form-label">Файлы</label>
                                <input type="file" class="form-control" id="file" name="file" multiple>
                                <div class="form-text">Максимальный размер файла: 10 МБ</div>
                            </div>
                            <div class="mb-3 form-check">
                                <input type="checkbox" class="form-check-input" id="needToPerform" name="needToPerform">
                                <label class="form-check-label" for="needToPerform">Требуется выполнение</label>
                            </div>
                            <div class="mb-3">
                                <label for="deadline" class="form-label">Срок выполнения</label>
                                <input type="datetime-local" class="form-control" id="deadline" name="deadline">
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Отмена</button>
                            <button type="submit" class="btn btn-primary">Сохранить</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <!-- Модальное окно для просмотра сообщения -->
        <div class="modal fade" id="viewMessageModal" tabindex="-1" aria-labelledby="viewMessageModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="viewMessageModalLabel">Сообщение к занятию</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <div class="mb-3">
                            <label class="form-label">Дисциплина: <span id="viewModalDiscipline"></span></label><br>
                            <label class="form-label">Группа: <span id="viewModalGroup"></span></label><br>
                            <label class="form-label">Дата: <span id="viewModalDate"></span></label>
                        </div>
                        <div id="messagesList">
                            <#list lessonMessages as key, message>
                                <div class="card mb-3 message-card" data-message-key="${key}" style="display: none;">
                                    <div class="card-body">
                                        <p class="card-text">${message.textMessage}</p>
                                        <#if message.file?? && message.file?has_content>
                                            <p class="mb-1"><strong>Прикрепленные файлы:</strong></p>
                                            <#list message.file?split(";") as fileName>
                                                <a href="/files/${fileName}" class="btn btn-sm btn-outline-primary mb-1">
                                                    <i class="bi bi-download"></i> ${fileName?keep_after_last("/")}
                                                </a>
                                            </#list>
                                        </#if>
                                        <#if message.needToPerform>
                                            <div class="mt-2">
                                                <span class="badge bg-warning text-dark">
                                                    <i class="bi bi-exclamation-triangle"></i> Требуется выполнение
                                                </span>
                                                <#if message.formattedDeadline??>
                                                    <span class="badge bg-info">
                                                        <i class="bi bi-clock"></i> Срок: ${message.formattedDeadline}
                                                    </span>
                                                </#if>
                                            </div>
                                        </#if>
                                        <div class="mt-3">
                                            <button type="button" class="btn btn-primary" 
                                                    data-bs-toggle="modal" 
                                                    data-bs-target="#editMessageModal"
                                                    data-message-id="${message.id}"
                                                    data-text="${message.textMessage}"
                                                    data-need-perform="${message.needToPerform?string}"
                                                    data-deadline="${message.deadline!''}"
                                                    data-files="${message.file!''}"
                                                    onclick="prepareEditForm(this)">
                                                <i class="bi bi-pencil"></i> Редактировать
                                            </button>
                                            <button type="button" class="btn btn-danger" 
                                                    onclick="deleteMessage(${message.id})">
                                                <i class="bi bi-trash"></i> Удалить
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            </#list>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Закрыть</button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Модальное окно для редактирования сообщения -->
        <div class="modal fade" id="editMessageModal" tabindex="-1" aria-labelledby="editMessageModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <form id="editMessageForm" action="/editLessonMessage" method="POST" enctype="multipart/form-data">
                        <div class="modal-header">
                            <h5 class="modal-title" id="editMessageModalLabel">Редактировать сообщение</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <input type="hidden" id="editMessageId" name="messageId">
                            <input type="hidden" id="editWeekShift" name="weekShift" value="${weekShift}">
                            <div class="mb-3">
                                <label for="editMessageText" class="form-label">Текст сообщения</label>
                                <textarea class="form-control" id="editMessageText" name="textMessage" rows="3" maxlength="2048" required></textarea>
                            </div>
                            <div class="mb-3">
                                <label for="editFile" class="form-label">Файлы</label>
                                <input type="file" class="form-control" id="editFile" name="file" multiple>
                                <div class="form-text">Максимальный размер файла: 10 МБ</div>
                            </div>
                            <div class="mb-3 form-check">
                                <input type="checkbox" class="form-check-input" id="editNeedToPerform" name="needToPerform">
                                <label class="form-check-label" for="editNeedToPerform">Требуется выполнение</label>
                            </div>
                            <div class="mb-3">
                                <label for="editDeadline" class="form-label">Срок выполнения</label>
                                <input type="datetime-local" class="form-control" id="editDeadline" name="deadline">
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Отмена</button>
                            <button type="submit" class="btn btn-primary">Сохранить изменения</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <script>
            document.addEventListener('DOMContentLoaded', function() {
                const messageModal = document.getElementById('messageModal');
                const viewMessageModal = document.getElementById('viewMessageModal');
                const editMessageModal = document.getElementById('editMessageModal');
                let isEditMode = false;

                messageModal.addEventListener('show.bs.modal', function(event) {
                    const button = event.relatedTarget;
                    const lessonId = button.getAttribute('data-lesson-id');
                    const discipline = button.getAttribute('data-discipline');
                    const group = button.getAttribute('data-group');
                    const date = button.getAttribute('data-date');

                    document.getElementById('lessonId').value = lessonId;
                    document.getElementById('modalDiscipline').textContent = discipline;
                    document.getElementById('modalGroup').textContent = group;
                    document.getElementById('modalDate').textContent = date;
                });

                viewMessageModal.addEventListener('show.bs.modal', function(event) {
                    const button = event.relatedTarget;
                    const discipline = button.getAttribute('data-discipline');
                    const group = button.getAttribute('data-group');
                    const date = button.getAttribute('data-date');
                    const lessonKey = button.getAttribute('data-lesson-key');

                    document.getElementById('viewModalDiscipline').textContent = discipline;
                    document.getElementById('viewModalGroup').textContent = group;
                    document.getElementById('viewModalDate').textContent = date;

                    // Скрыть все карточки сообщений
                    document.querySelectorAll('.message-card').forEach(card => {
                        const cardKey = card.getAttribute('data-message-key');
                        if (cardKey === lessonKey) {
                            card.style.display = 'block';
                        } else {
                            card.style.display = 'none';
                        }
                    });
                });

                // При открытии окна редактирования
                editMessageModal.addEventListener('show.bs.modal', function(event) {
                    isEditMode = true;
                });

                // При закрытии окна редактирования
                editMessageModal.addEventListener('hidden.bs.modal', function(event) {
                    document.getElementById('editMessageForm').reset();
                    if (isEditMode) {
                        const viewModal = bootstrap.Modal.getInstance(viewMessageModal) || new bootstrap.Modal(viewMessageModal);
                        viewModal.show();
                    }
                    isEditMode = false;
                });

                // При закрытии окна просмотра
                viewMessageModal.addEventListener('hide.bs.modal', function(event) {
                    // Если мы не переходим в режим редактирования, закрываем все модальные окна
                    if (!isEditMode) {
                        const modals = [messageModal, viewMessageModal, editMessageModal].map(modal => 
                            bootstrap.Modal.getInstance(modal)
                        ).filter(Boolean);
                        
                        modals.forEach(modal => modal.hide());
                    }
                });
            });

            function prepareEditForm(button) {
                const messageId = button.getAttribute('data-message-id');
                const text = button.getAttribute('data-text');
                const needPerform = button.getAttribute('data-need-perform') === 'true';
                const deadline = button.getAttribute('data-deadline');

                document.getElementById('editMessageId').value = messageId;
                document.getElementById('editMessageText').value = text;
                document.getElementById('editNeedToPerform').checked = needPerform;
                
                if (deadline) {
                    const deadlineDate = new Date(deadline);
                    const formattedDeadline = deadlineDate.toISOString().slice(0, 16);
                    document.getElementById('editDeadline').value = formattedDeadline;
                } else {
                    document.getElementById('editDeadline').value = '';
                }
            }

            // Функция удаления сообщения
            function deleteMessage(messageId) {
                if (confirm('Вы уверены, что хотите удалить это сообщение?')) {
                    const form = document.createElement('form');
                    form.method = 'POST';
                    form.action = '/deleteLessonMessage';

                    const messageIdInput = document.createElement('input');
                    messageIdInput.type = 'hidden';
                    messageIdInput.name = 'messageId';
                    messageIdInput.value = messageId;
                    form.appendChild(messageIdInput);

                    const weekShiftInput = document.createElement('input');
                    weekShiftInput.type = 'hidden';
                    weekShiftInput.name = 'weekShift';
                    weekShiftInput.value = '${weekShift}';
                    form.appendChild(weekShiftInput);

                    document.body.appendChild(form);
                    form.submit();
                }
            }
        </script>
    </#if>
</@c.page>