<#import "parts/commonTmpl.ftlh" as c>
<#include "parts/securityHlpr.ftlh">

<@c.page>
    <#if isStudent == 0>
        <h4>Для дальнейшей работы заполните данные о профиле и дождитесь получения статуса от администратора сайта.</h4>
    <#else>
        <#if errorMessage??>
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                ${errorMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </#if>

        <div class="card mb-5">
            <div class="card-header">
                <div class="d-flex justify-content-between align-items-center">
                    <h5 class="card-title mb-0">Расписание группы ${selectedGroup.nameGroup}</h5>
                    <div class="btn-group">
                        <a href="/studentSchedule?weekShift=${weekShift - 1}" class="btn btn-outline-primary">
                            <i class="bi bi-chevron-left"></i> Предыдущая неделя
                        </a>
                        <a href="/studentSchedule?weekShift=0" class="btn btn-outline-primary <#if weekShift == 0>active</#if>">
                            Текущая неделя
                        </a>
                        <a href="/studentSchedule?weekShift=${weekShift + 1}" class="btn btn-outline-primary">
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
                                                                <i class="bi bi-person"></i> ${lesson.teacher.user.fio!lesson.teacher.user.username}<br>
                                                                <i class="bi bi-geo-alt"></i> ${lesson.classroom!''}<br>
                                                                <#if lesson.lessonType??><i class="bi bi-journal-text"></i> ${lesson.lessonType.type}</#if>
                                                            </small>
                                                        </p>
                                                        <#if lessonMessages?? && lessonMessages[lessonKey]??>
                                                            <button type="button" class="btn btn-sm btn-outline-primary mt-1"
                                                                    data-bs-toggle="modal" 
                                                                    data-bs-target="#viewMessageModal"
                                                                    data-lesson-id="${lesson.id}"
                                                                    data-lesson-key="${lessonKey}"
                                                                    data-discipline="${lesson.discipline.nameDiscipline}"
                                                                    data-teacher="${lesson.teacher.user.fio!lesson.teacher.user.username}"
                                                                    data-date="${date?replace("Z", "")?replace("T", " ")?datetime("yyyy-MM-dd")?string('dd.MM.yyyy')}"
                                                                    data-pair-number="${pair.id}">
                                                                <i class="bi bi-envelope"></i> Сообщение к занятию
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
                            <label class="form-label">Преподаватель: <span id="viewModalTeacher"></span></label><br>
                            <label class="form-label">Дата: <span id="viewModalDate"></span>, пара №<span id="viewModalPairNumber"></span></label>
                        </div>
                        <div id="messagesList">
                            <#if lessonMessages??>
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
                                            <#if message.formattedDeadline??>
                                                <div class="mt-2">
                                                    <span class="badge bg-info">
                                                        <i class="bi bi-clock"></i> Срок: ${message.formattedDeadline}
                                                    </span>
                                                    <#if message.needToPerform>
                                                        <span class="badge bg-warning text-dark">
                                                            <i class="bi bi-exclamation-triangle"></i> Требуется выполнение
                                                        </span>
                                                    </#if>
                                                </div>
                                            </#if>
                                            <#if message.needToPerform>
                                                <div class="mt-3 pt-3 border-top text-end">
                                                    <a href="/submit/${message.id}?returnUrl=/studentSchedule?weekShift=${weekShift}" class="btn btn-success">
                                                        <i class="bi bi-check2-circle"></i> Сдать задание
                                                    </a>
                                                </div>
                                            </#if>
                                        </div>
                                    </div>
                                </#list>
                            </#if>
                        </div>
                    </div>
                    
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Закрыть</button>
                    </div>
                </div>
            </div>
        </div>

        <script>
            document.addEventListener('DOMContentLoaded', function() {
                const viewMessageModal = document.getElementById('viewMessageModal');

                viewMessageModal.addEventListener('show.bs.modal', function(event) {
                    const button = event.relatedTarget;
                    const discipline = button.getAttribute('data-discipline');
                    const teacher = button.getAttribute('data-teacher');
                    const date = button.getAttribute('data-date');
                    const pairNumber = button.getAttribute('data-pair-number');
                    const lessonKey = button.getAttribute('data-lesson-key');

                    document.getElementById('viewModalDiscipline').textContent = discipline;
                    document.getElementById('viewModalTeacher').textContent = teacher;
                    document.getElementById('viewModalDate').textContent = date;
                    document.getElementById('viewModalPairNumber').textContent = pairNumber;

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
            });
        </script>
    </#if>
</@c.page>