<#import "parts/commonTmpl.ftlh" as c>
<#include "parts/securityHlpr.ftlh">

<@c.page>
    <div class="mb-4">
        <a class="btn btn-outline-primary" href="/studentSchedule">
            Вернуться к расписанию
        </a>
    </div>

    <div class="mb-3">Группа: <strong>${group}</strong></div>

    <div class="row">
        <div class="col-12">
            <div class="card mb-5">
                <div class="card-header">
                    <h4 class="card-title mb-0">Оценки по дисциплинам</h4>
                </div>
                <div class="card-body">
                    <div class="custom-accordion" id="disciplinesAccordion">
                        <#list disciplines as discipline>
                            <#assign disciplineMarks = studentMarks[discipline.id?string]![]>
                            <#assign hasValidMarks = false>
                            <#assign totalMark = 0>
                            <#assign markCount = 0>
                            <#if disciplineMarks?has_content>
                                <#list disciplineMarks as mark>
                                    <#if mark.mark?? && mark.mark?is_number>
                                        <#assign totalMark = totalMark + mark.mark>
                                        <#assign markCount = markCount + 1>
                                        <#assign hasValidMarks = true>
                                    </#if>
                                </#list>
                            </#if>
                            <div class="accordion-item">
                                <h2 class="accordion-header" id="heading_${discipline.id}">
                                    <button class="accordion-button collapsed" type="button"
                                            data-target="#collapse_${discipline.id}"
                                            aria-expanded="false"
                                            aria-controls="collapse_${discipline.id}">
                                        <div class="d-flex justify-content-between align-items-center w-100 me-4">
                                            <span class="fs-5">${discipline.nameDiscipline}</span>
                                            <#if hasValidMarks>
                                                <span class="badge bg-primary ms-2">
                                                    Средний балл: ${(totalMark/markCount)?string("0.##")}
                                                </span>
                                            <#else>
                                                <span class="badge bg-secondary ms-2">Нет оценок</span>
                                            </#if>
                                        </div>
                                    </button>
                                </h2>
                                <div id="collapse_${discipline.id}"
                                     class="accordion-collapse collapse"
                                     aria-labelledby="heading_${discipline.id}">
                                    <div class="accordion-body p-0">
                                        <div class="table-responsive">
                                            <#if disciplineMarks?has_content>
                                                <table class="table table-hover mb-0">
                                                    <thead>
                                                        <tr class="text-center table-light border-top">
                                                            <th style="width: 20%">Дата занятия</th>
                                                            <th style="width: 15%">Оценка</th>
                                                            <th style="width: 65%">Комментарий</th>
                                                        </tr>
                                                    </thead>
                                                    <tbody>
                                                        <#list disciplineMarks as mark>
                                                            <tr class="text-center table-light">
                                                                <td class="align-middle">
                                                                    <#if mark.task?? && mark.task.lessonMessage?? && mark.task.lessonMessage.lessons?? && mark.task.lessonMessage.lessons.date??>
                                                                        <#assign taskId = mark.task.id?string>
                                                                        <#assign disciplineId = discipline.id?string>
                                                                        <#assign taskInfo = (taskDetails[disciplineId][taskId])!{}>
                                                                        <a href="#" class="text-decoration-none" data-bs-toggle="modal" data-bs-target="#taskModal"
                                                                           data-discipline="${discipline.nameDiscipline}"
                                                                           data-date="${mark.task.lessonMessage.lessons.date?datetime("yyyy-MM-dd")?string("dd.MM.yyyy")}"
                                                                           data-pair="${mark.task.lessonMessage.lessons.pair.id}"
                                                                           data-task-text="${(taskInfo.textMessage)!''}"
                                                                           data-deadline="${(taskInfo.deadline?? && taskInfo.deadline != "")?then(taskInfo.deadline, '')}"
                                                                           data-need-perform="${(taskInfo.needToPerform??)?then(taskInfo.needToPerform?string, 'false')}"
                                                                           <#if taskInfo.files?? && taskInfo.files?has_content>data-files="${taskInfo.files}"</#if>>
                                                                            ${mark.task.lessonMessage.lessons.date?datetime("yyyy-MM-dd")?string("dd.MM.yyyy")}
                                                                            <br>
                                                                            (пара №${mark.task.lessonMessage.lessons.pair.id})
                                                                        </a>
                                                                    <#else>
                                                                        Дата не указана
                                                                    </#if>
                                                                </td>
                                                                <td class="align-middle">
                                                                    <#if mark.mark??>
                                                                        <span class="badge fs-6 ${(mark.mark == 2)?then('bg-danger', 
                                                                            (mark.mark == 3)?then('bg-warning', 
                                                                            (mark.mark >= 4)?then('bg-success', 'bg-success')))}">
                                                                            ${mark.mark}
                                                                        </span>
                                                                    <#else>
                                                                        <span class="badge bg-secondary fs-6">Нет оценки</span>
                                                                    </#if>
                                                                </td>
                                                                <td class="align-middle text-center">
                                                                    <#if mark.comment?? && mark.comment != "">
                                                                        ${mark.comment}
                                                                    <#else>
                                                                        Комментарий отсутствует
                                                                    </#if>
                                                                </td>
                                                            </tr>
                                                        </#list>
                                                    </tbody>
                                                </table>
                                            <#else>
                                                <table class="table mb-0">
                                                    <tbody>
                                                        <tr class="text-center table-light border-top">
                                                            <td class="py-3">По данной дисциплине оценок пока нет</td>
                                                        </tr>
                                                    </tbody>
                                                </table>
                                            </#if>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </#list>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Модальное окно для просмотра информации о задании -->
    <div class="modal fade" id="taskModal" tabindex="-1" aria-labelledby="taskModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="taskModalLabel">Информация о задании</h5>
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

    <style>
        .custom-accordion .accordion-button {
            padding: 1rem;
            background-color: #fff;
            border: none;
            width: 100%;
            text-align: left;
            position: relative;
            display: flex;
            align-items: center;
            transition: all 0.2s ease;
        }

        .custom-accordion .accordion-button:hover {
            background-color: #cde1ff !important;
        }
        
        .custom-accordion .accordion-button:not(.collapsed) {
            background-color: #e7f1ff !important;
        }

        /* Добавляем специальный стиль для наведения на активную кнопку */
        .custom-accordion .accordion-button:not(.collapsed):hover {
            background-color: #cde1ff !important;
        }
        
        .custom-accordion .accordion-button::after {
            content: '';
            width: 24px;
            height: 24px;
            position: absolute;
            right: 1rem;
            background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='16' height='16' fill='currentColor' viewBox='0 0 16 16'%3E%3Cpath fill-rule='evenodd' d='M1.646 4.646a.5.5 0 0 1 .708 0L8 10.293l5.646-5.647a.5.5 0 0 1 .708.708l-6 6a.5.5 0 0 1-.708 0l-6-6a.5.5 0 0 1 0-.708z'/%3E%3C/svg%3E");
            background-repeat: no-repeat;
            background-position: center;
            background-size: 16px;
            transition: transform 0.2s ease;
        }
        
        .custom-accordion .accordion-button:not(.collapsed)::after {
            transform: rotate(-180deg);
        }
        
        .custom-accordion .accordion-button:focus {
            box-shadow: none !important;
            border-color: rgba(25, 135, 84, 0.25) !important;
        }
        
        .custom-accordion .accordion-item {
            border-radius: 4px !important;
            margin-bottom: 4px !important;
            border: 1px solid rgba(0,0,0,.125) !important;
            overflow: hidden;
        }

        .custom-accordion .accordion-collapse {
            border-top: 1px solid rgba(0,0,0,.125);
        }

        .table > :not(caption) > * > * {
            padding: 0.75rem;
        }

        .table-light {
            --bs-table-bg: rgba(248, 249, 250, 0.6);
        }

        .table-hover tbody tr:hover {
            --bs-table-accent-bg: rgba(232, 245, 233, 0.4) !important;
        }

        /* Анимация для плавного открытия/закрытия */
        .custom-accordion .accordion-collapse {
            transition: all 0.2s ease;
        }

        .custom-accordion .accordion-collapse.collapse:not(.show) {
            display: none;
        }

        .custom-accordion .accordion-collapse.collapse.show {
            display: block;
        }
    </style>

    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const buttons = document.querySelectorAll('.accordion-button');
            const taskModal = document.getElementById('taskModal');
            
            buttons.forEach(button => {
                button.addEventListener('click', function(e) {
                    e.preventDefault();
                    e.stopPropagation();
                    
                    const targetId = this.getAttribute('data-target');
                    const targetPanel = document.querySelector(targetId);
                    
                    if (targetPanel) {
                        const isExpanded = this.getAttribute('aria-expanded') === 'true';
                        
                        // Переключаем текущую панель
                        if (isExpanded) {
                            targetPanel.classList.remove('show');
                            this.classList.add('collapsed');
                            this.setAttribute('aria-expanded', 'false');
                        } else {
                            targetPanel.classList.add('show');
                            this.classList.remove('collapsed');
                            this.setAttribute('aria-expanded', 'true');
                        }
                    }
                });
            });

            // Обработчик для модального окна
            taskModal.addEventListener('show.bs.modal', function(event) {
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
                document.getElementById('modalTaskText').textContent = taskText || 'Текст задания отсутствует';

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
                        if (fileName.trim() !== '') {
                            const fileLink = document.createElement('a');
                            fileLink.href = '/files/' + fileName;
                            fileLink.className = 'btn btn-sm btn-outline-primary mb-1 me-1';
                            const shortName = fileName.substring(fileName.lastIndexOf('/') + 1);
                            fileLink.innerHTML = '<i class="bi bi-download"></i> ' + shortName;
                            filesList.appendChild(fileLink);
                        }
                    });
                    modalFiles.style.display = 'block';
                } else {
                    modalFiles.style.display = 'none';
                }
            });
        });
    </script>
</@c.page>