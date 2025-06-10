<#import "parts/commonTmpl.ftlh" as c>
<#include "parts/securityHlpr.ftlh">

<@c.page>
    <div class="mb-4">
        <a class="btn btn-outline-primary" href="/teacherSchedule">
            Вернуться к расписанию
        </a>
    </div>

    <div class="row mb-3">
        <div class="col-md-6">
            <h6 class="mt-1">Выберите группу для проверки заданий:</h6>
            <form method="get" action="/groups" class="form-inline">
                <div class="input-group">
                    <select class="form-select" name="selectedGroup" id="selectedGroup">
                        <option value="">Выберите группу</option>
                        <#list groups as group>
                            <option value="${group.id}" <#if selectedGroup?? && selectedGroup.id == group.id>selected</#if>>
                                ${group.nameGroup}
                            </option>
                        </#list>
                    </select>
                    <button type="submit" class="btn btn-primary">Показать список группы</button>
                </div>
            </form>
        </div>
    </div>

    <#if selectedGroup??>
        <div class="row mb-3">
            <div class="col-md-6">
                <h6 class="mt-1">Выберите дисциплину для отображения заданий:</h6>
                <form method="get" action="/groups" class="form-inline">
                    <input type="hidden" name="selectedGroup" value="${selectedGroup.id}">
                    <div class="input-group">
                        <select class="form-select" name="selectedDiscipline" id="selectedDiscipline">
                            <option value="">Выберите дисциплину</option>
                            <#if groupDisciplines?? && groupDisciplines?has_content>
                                <#list groupDisciplines as discipline>
                                    <option value="${discipline.id}" <#if selectedDiscipline?? && selectedDiscipline.id == discipline.id>selected</#if>>
                                        ${discipline.nameDiscipline}
                                    </option>
                                </#list>
                            </#if>
                        </select>
                        <button type="submit" class="btn btn-primary">Показать задания</button>
                    </div>
                </form>
            </div>
        </div>

        <div class="card mb-5">
            <div class="card-header">
                <h5 class="card-title mb-0">Список студентов группы ${selectedGroup.nameGroup}</h5>
            </div>
            <div class="card-body">
                <#if students?? && students?has_content>
                    <div class="table-responsive">
                        <table class="table table-hover table-bordered">
                            <thead>
                                <tr>
                                    <th class="text-center align-middle" <#if selectedDiscipline??>rowspan="2"</#if>>№ студенческого</th>
                                    <th class="text-center align-middle" <#if selectedDiscipline??>rowspan="2"</#if>>ФИО</th>
                                    <#if selectedDiscipline??>
                                        <#if tasks?? && tasks?has_content>
                                            <th colspan="${tasks?size}" class="text-center">Задания</th>
                                        <#else>
                                            <th class="text-center">Задания</th>
                                        </#if>
                                    </#if>
                                </tr>
                                <#if selectedDiscipline?? && tasks?? && tasks?has_content>
                                    <tr>
                                        <#list tasks as task>
                                            <th class="text-center">
                                                <a href="#" class="text-decoration-none" data-bs-toggle="modal" data-bs-target="#viewMessageModal"
                                                   data-lesson-id="${task.lessons.id}"
                                                   data-lesson-key="${task.lessons.date?datetime("yyyy-MM-dd")?string('yyyy-MM-dd')}_${task.lessons.pair.id}"
                                                   data-discipline="${selectedDiscipline.nameDiscipline}"
                                                   data-date="${task.lessons.date?datetime("yyyy-MM-dd")?string('dd.MM.yyyy')}"
                                                   data-pair="${task.lessons.pair.id}"
                                                   data-task-text="${task.textMessage!''}"
                                                   data-deadline="${task.formattedDeadline!''}"
                                                   data-need-perform="${task.needToPerform?string}"
                                                   data-files="${task.file!''}">
                                                    ${task.lessons.date?datetime("yyyy-MM-dd")?string('dd.MM.yyyy')}
                                                    <br>
                                                    (пара №${task.lessons.pair.id})
                                                </a>
                                            </th>
                                        </#list>
                                    </tr>
                                </#if>
                            </thead>
                            <tbody>
                                <#list students as student>
                                    <tr>
                                        <td class="text-center">${student.studentTicket!''}</td>
                                        <td class="text-center">
                                            <#if student.user??>
                                                ${student.user.fio}
                                            <#else>
                                                Нет данных
                                            </#if>
                                        </td>
                                        <#if selectedDiscipline??>
                                            <#if tasks?? && tasks?has_content>
                                                <#list tasks as task>
                                                    <td class="text-center">
                                                        <#assign submission = submissions[student.id?string + '_' + task.id?string]!>
                                                        <#if studentGrades[student.id?string]?? && studentGrades[student.id?string][task.id?string]??>
                                                            <a href="/submit/${task.id}?studentId=${student.id}&returnUrl=${('/groups?selectedGroup=' + selectedGroup.id + '&selectedDiscipline=' + selectedDiscipline.id)?url}" 
                                                               class="text-decoration-none">
                                                                <span class="badge ${(studentGrades[student.id?string][task.id?string]?string == "2")?then('bg-danger', (studentGrades[student.id?string][task.id?string]?string == "3")?then('bg-warning', 'bg-success'))} fs-6">${studentGrades[student.id?string][task.id?string]}</span>
                                                            </a>
                                                        <#elseif submission.statusTask?? && submission.statusTask.id == 3>
                                                            <a href="/submit/${task.id}?studentId=${student.id}&returnUrl=${('/groups?selectedGroup=' + selectedGroup.id + '&selectedDiscipline=' + selectedDiscipline.id)?url}" 
                                                               class="btn btn-sm btn-outline-success">
                                                                <i class="bi bi-check2"></i> Проверить
                                                            </a>
                                                        <#else>
                                                            <a href="/submit/${task.id}?studentId=${student.id}&returnUrl=${('/groups?selectedGroup=' + selectedGroup.id + '&selectedDiscipline=' + selectedDiscipline.id)?url}" 
                                                               class="btn btn-sm btn-outline-danger">
                                                                <i class="bi bi-check2"></i> Проверить
                                                            </a>
                                                        </#if>
                                                    </td>
                                                </#list>
                                            <#else>
                                                <td class="text-center">Нет заданий</td>
                                            </#if>
                                        </#if>
                                    </tr>
                                </#list>
                            </tbody>
                        </table>
                    </div>

                    <!-- Модальное окно для просмотра информации о задании -->
                    <div class="modal fade" id="viewMessageModal" tabindex="-1" aria-labelledby="viewMessageModalLabel" aria-hidden="true">
                        <div class="modal-dialog modal-lg">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h5 class="modal-title" id="viewMessageModalLabel">Информация о задании</h5>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                </div>
                                <div class="modal-body">
                                    <div class="mb-3">
                                        <label class="form-label">Дисциплина: <span id="viewModalDiscipline"></span></label><br>
                                        <label class="form-label">Дата: <span id="viewModalDate"></span>, пара №<span id="viewModalPairNumber"></span></label>
                                    </div>
                                    <div class="card mb-3">
                                        <div class="card-body">
                                            <p class="card-text" id="modalTaskText"></p>
                                            <div id="modalFiles" style="display: none;">
                                                <p class="mb-1"><strong>Прикрепленные файлы:</strong></p>
                                                <div id="filesList"></div>
                                            </div>
                                            <div class="mt-2" id="modalBadges">
                                                <span class="badge bg-info" id="deadlineBadge" style="display: none;">
                                                    <i class="bi bi-clock"></i> Срок: <span id="modalDeadline"></span>
                                                </span>
                                                <span class="badge bg-warning text-dark" id="needPerformBadge" style="display: none;">
                                                    <i class="bi bi-exclamation-triangle"></i> Требуется выполнение
                                                </span>
                                            </div>
                                        </div>
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
                                const date = button.getAttribute('data-date');
                                const pair = button.getAttribute('data-pair');
                                const taskText = button.getAttribute('data-task-text');
                                const deadline = button.getAttribute('data-deadline');
                                const needPerform = button.getAttribute('data-need-perform') === 'true';
                                const files = button.getAttribute('data-files');

                                document.getElementById('viewModalDiscipline').textContent = discipline;
                                document.getElementById('viewModalDate').textContent = date;
                                document.getElementById('viewModalPairNumber').textContent = pair;
                                document.getElementById('modalTaskText').textContent = taskText;

                                // Обработка срока выполнения
                                const deadlineBadge = document.getElementById('deadlineBadge');
                                if (deadline) {
                                    document.getElementById('modalDeadline').textContent = deadline;
                                    deadlineBadge.style.display = 'inline-block';
                                } else {
                                    deadlineBadge.style.display = 'none';
                                }

                                // Обработка метки "Требуется выполнение"
                                const needPerformBadge = document.getElementById('needPerformBadge');
                                needPerformBadge.style.display = needPerform ? 'inline-block' : 'none';

                                // Обработка файлов
                                const modalFiles = document.getElementById('modalFiles');
                                const filesList = document.getElementById('filesList');
                                if (files && files.trim() !== '') {
                                    filesList.innerHTML = '';
                                    files.split(';').forEach(fileName => {
                                        const fileLink = document.createElement('a');
                                        fileLink.href = '/files/' + fileName;
                                        fileLink.className = 'btn btn-sm btn-outline-primary mb-1 me-1';
                                        const shortName = fileName.substring(fileName.lastIndexOf('/') + 1);
                                    fileLink.innerHTML = '<i class="bi bi-download"></i> ' + shortName;
                                        filesList.appendChild(fileLink);
                                    });
                                    modalFiles.style.display = 'block';
                                } else {
                                    modalFiles.style.display = 'none';
                                }
                            });
                        });
                    </script>
                <#else>
                    <p class="text-muted mb-0">В данной группе нет студентов</p>
                </#if>
            </div>
        </div>
    </#if>
</@c.page>