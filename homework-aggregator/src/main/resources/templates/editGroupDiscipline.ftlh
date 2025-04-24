<#import "parts/commonTmpl.ftlh" as c>

<@c.page>
    <a class="btn btn-outline-primary mb-3" href="/editGroup">
        Вернуться назад
    </a>

    <div class="card card-body col-sm-6 mb-1">
        <h5 class="card-title">Назначение дисциплин группе: ${group.nameGroup}</h5>
        <form action="/editGroupDiscipline" method="post">
            <input type="hidden" name="groupId" value="${group.id}">

            <#if message1??>
                <div class="alert alert-${messageType1!"info"} alert-dismissible fade show" role="alert">
                    ${message1}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </#if>

            <div id="disciplinesForm">
                <h6 class="mt-3">Выберите дисциплины:</h6>
                <div class="form-group">
                    <div class="list-group" id="disciplinesList">
                        <#list disciplines as discipline>
                            <div class="list-group-item">
                                <div class="form-check">
                                    <input class="form-check-input" type="checkbox"
                                           name="disciplineIds"
                                           value="${discipline.id}"
                                           id="discipline_${discipline.id}"
                                           <#if groupDisciplines?seq_contains(discipline)>checked</#if>>

                                    <label class="form-check-label" for="discipline_${discipline.id}">
                                        ${discipline.nameDiscipline}
                                    </label>
                                </div>
                            </div>
                        </#list>
                    </div>
                </div>
            </div>

            <button type="submit" class="btn btn-primary mt-3" id="submitBtn">Сохранить</button>
        </form>
    </div>
</@c.page>