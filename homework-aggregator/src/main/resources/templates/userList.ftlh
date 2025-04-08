<#import "parts/commonTmpl.ftlh" as c>

<@c.page>
    <a class="btn btn-outline-primary mb-3" href="/main">
        Вернуться назад
    </a>

    <table class="table table-striped">
        <thead>
        <tr>
            <th>Логин</th>
            <th>ФИО</th>
            <th>Статус</th>
            <th>Роль</th>
            <th>Активный</th>
            <th></th>
            <th></th>
        </tr>
        </thead>
        <tbody class="table-group-divider">
        <#list users as user>
            <tr>
                <td>${user.username}</td>
                <td>${user.fio! 'Не указано'}</td>
                <td>${user.status!'Не назначен'}</td>
                <td>${user.role}</td>
                <td><#if user.active??>Действующий аккаунт<#else> Заблокированный</#if></td>
                <#if user.id != currentUserId>
                    <td><a href="/user/${user.id}">Изменить</a></td>
                </#if>
                <td>
                    <form action="/user/delete" method="post">
                        <input type="hidden" value="${user.id}" name="userId">
                        <#if user.id != currentUserId>
                            <button class="btn btn-primary" type="submit">Удалить</button>
                        </#if>
                    </form>
                </td>
            </tr>
        </#list>
        </tbody>
    </table>
</@c.page>