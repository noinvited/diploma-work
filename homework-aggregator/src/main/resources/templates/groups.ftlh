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
                                            <th class="text-center">${task.lessons.date?datetime("yyyy-MM-dd")?string('dd.MM.yyyy')}</th>
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
                                                    <td class="text-center"></td>
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
                <#else>
                    <p class="text-muted mb-0">В данной группе нет студентов</p>
                </#if>
            </div>
        </div>
    </#if>
</@c.page>