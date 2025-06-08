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
                    <div class="accordion" id="disciplinesAccordion">
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
                                    <button class="accordion-button fw-bold collapsed" type="button"
                                            data-bs-toggle="collapse"
                                            data-bs-target="#collapse_${discipline.id}"
                                            aria-expanded="false"
                                            aria-controls="collapse_${discipline.id}">
                                        <div class="d-flex justify-content-between align-items-center w-100 me-4">
                                            <span>${discipline.nameDiscipline}</span>
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
                                     aria-labelledby="heading_${discipline.id}"
                                     data-bs-parent="#disciplinesAccordion">
                                    <div class="accordion-body">
                                        <#if disciplineMarks?has_content>
                                            <table class="table table-hover mb-0">
                                                <thead>
                                                <tr>
                                                    <th>Дата занятия</th>
                                                    <th>Задание</th>
                                                    <th>Оценка</th>
                                                    <th>Комментарий</th>
                                                </tr>
                                                </thead>
                                                <tbody>
                                                <#list disciplineMarks as mark>
                                                    <tr>
                                                        <td>${mark.task.lessonMessage.lessons.date?string('dd.MM.yyyy')}</td>
                                                        <td>${mark.task.lessonMessage.textMessage!''}</td>
                                                        <td>${mark.mark!'Нет оценки'}</td>
                                                        <td>${mark.comment!''}</td>
                                                    </tr>
                                                </#list>
                                                </tbody>
                                            </table>
                                        <#else>
                                            <p class="text-muted mb-0">По данной дисциплине оценок пока нет</p>
                                        </#if>
                                    </div>
                                </div>
                            </div>
                        </#list>
                    </div>
                </div>
            </div>
        </div>
    </div>
</@c.page>