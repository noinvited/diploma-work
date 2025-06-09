<#import "parts/commonTmpl.ftlh" as c>
<#include "parts/securityHlpr.ftlh">

<@c.page>
    <#if errorMessage??>
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            ${errorMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </#if>

    <div class="mb-4">
        <a class="btn btn-outline-primary" href="/studentSchedule">
            Вернуться к расписанию
        </a>
    </div>

    <div class="card mb-4">
        <div class="card-header">
            <h5 class="card-title mb-0">Фильтры</h5>
        </div>
        <div class="card-body">
            <form method="get" action="/studentTasks" class="row g-3">
                <div class="col-md-4">
                    <label for="disciplineId" class="form-label">Предмет</label>
                    <select class="form-select" id="disciplineId" name="disciplineId">
                        <option value="">Все предметы</option>
                        <#list disciplines as discipline>
                            <option value="${discipline.id}"
                                    <#if selectedDiscipline?? && selectedDiscipline == discipline.id>selected</#if>>
                                ${discipline.nameDiscipline}
                            </option>
                        </#list>
                    </select>
                </div>
                <div class="col-md-4">
                    <label for="needToPerform" class="form-label">Тип сообщения</label>
                    <select class="form-select" id="needToPerform" name="needToPerform">
                        <option value="">Все сообщения</option>
                        <option value="true" <#if selectedNeedToPerform?? && selectedNeedToPerform>selected</#if>>
                            Обязательные задания
                        </option>
                        <option value="false" <#if selectedNeedToPerform?? && !selectedNeedToPerform>selected</#if>>
                            Информационные сообщения
                        </option>
                    </select>
                </div>
                <div class="col-md-4">
                    <label for="status" class="form-label">Статус задания</label>
                    <select class="form-select" id="status" name="statusId">
                        <option value="">Любой статус</option>
                        <#list statuses as status>
                            <option value="${status.id}"
                                    <#if selectedStatus?? && selectedStatus == status.id>selected</#if>>
                                ${status.status}
                            </option>
                        </#list>
                    </select>
                </div>
                <div class="col-12">
                    <button type="submit" class="btn btn-primary">Применить фильтры</button>
                    <a href="/studentTasks" class="btn btn-outline-secondary">Сбросить</a>
                </div>
            </form>
        </div>
    </div>

    <div class="card mb-5">
        <div class="card-header">
            <h5 class="card-title mb-0">Задания</h5>
        </div>
        <div class="card-body">
            <#if messages?has_content>
                <div class="list-group">
                    <#list messages as message>
                        <div class="list-group-item border-0 mb-3 shadow-sm rounded">
                            <div class="d-flex w-100 justify-content-between align-items-center">
                                <h5 class="mb-1">${message.lessons.discipline.nameDiscipline}</h5>
                                <small class="text-muted">
                                    ${message.lessons.date?replace("Z", "")?replace("T", " ")?datetime("yyyy-MM-dd")?string('dd.MM.yyyy')}
                                    , пара №${message.lessons.pair.id}
                                </small>
                            </div>
                            <p class="mb-1">${message.textMessage}</p>
                            
                            <#if message.file?? && message.file?has_content>
                                <div class="mt-3">
                                    <#list message.file?split(";") as fileName>
                                        <a href="/files/${fileName}" class="btn btn-sm btn-outline-primary me-2">
                                            <i class="bi bi-download"></i> ${fileName?keep_after_last("/")}
                                        </a>
                                    </#list>
                                </div>
                            </#if>

                            <div class="d-flex justify-content-between align-items-center mt-3">
                                <small class="text-muted">
                                    Преподаватель: ${message.lessons.teacher.user.fio!message.lessons.teacher.user.username}
                                </small>
                                <div>
                                    <#if message.needToPerform>
                                        <span class="badge bg-primary">Обязательное задание</span>
                                        <#if message.deadline??>
                                            <span class="badge bg-info">
                                                Дедлайн: ${message.formattedDeadline!'Не указан'}
                                            </span>
                                        </#if>
                                        <#if submissions[message.id?string]?? && submissions[message.id?string].statusTask??>
                                            <span class="badge bg-secondary">
                                                ${submissions[message.id?string].statusTask.status}
                                            </span>
                                        </#if>
                                    <#else>
                                        <span class="badge bg-secondary">Информационное сообщение</span>
                                    </#if>
                                </div>
                            </div>

                            <#if message.needToPerform>
                                <div class="mt-3 pt-3 border-top text-end">
                                    <a href="/submit/${message.id}" class="btn btn-success">
                                        <i class="bi bi-check2-circle"></i> Сдать задание
                                    </a>
                                </div>
                            </#if>
                        </div>
                    </#list>
                </div>
            <#else>
                <p class="text-muted mb-0">Нет сообщений, соответствующих выбранным фильтрам</p>
            </#if>
        </div>
    </div>
</@c.page>