<#import "parts/commonTmpl.ftlh" as c>

<@c.page>
    <a class="btn btn-outline-primary mb-3" href="/user">
        Вернуться назад
    </a>

    <#if user.role == 'ADMIN'>
        <div class="card card-body col-sm-6">
            <form action="/user" method="post">
                <input type="text" name="username" class="form-control" value="${user.username}">
                <h6 class="mt-1">Роль пользователя:</h6>
                <div class="form-group mt-1">
                    <div class="form-check">
                        <input type="radio" name="role"  class="form-check-input"
                               value="USER" <#if user.role == 'USER'>checked</#if>>
                        <label class="form-check-label">
                            Пользователь
                        </label>
                    </div>
                    <div class="form-check">
                        <input type="radio" name="role" class="form-check-input"
                               value="ADMIN" <#if user.role == 'ADMIN'>checked</#if>>
                        <label class="form-check-label">
                            Администратор
                        </label>
                    </div>
                </div>

                <h6 class="mt-1">Статус пользователя:</h6>
                <div class="form-check form-switch mt-1">
                    <input name="status" class="form-check-input" type="checkbox" role="switch" id="flexSwitchCheck" <#if user.active>checked</#if>>
                    <label class="form-check-label" for="flexSwitchCheck">Заблокирован/Разблокирован</label>
                </div>
                <input type="hidden" value="${user.id}" name="userId">
                <button type="submit" class="btn btn-primary mt-3">Сохранить</button>
            </form>
        </div>
    <#else>

    </#if>
</@c.page>