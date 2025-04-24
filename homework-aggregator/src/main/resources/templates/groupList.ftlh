<#import "parts/commonTmpl.ftlh" as c>

<@c.page>
    <a class="btn btn-outline-primary mb-3" href="/">
        Вернуться в главное меню
    </a>

    <#if message1??>
        <div class="alert alert-${messageType1}" role="alert">
            ${message1}
        </div>
    </#if>

    <div class="card mb-4 w-50">
        <div class="card-header">
            <h5>Добавить новую группу</h5>
        </div>
        <div class="card-body">
            <form action="/group/add" method="post">
                <div class="mb-3">
                    <label for="groupName" class="form-label">Введите название группы:</label>
                    <input type="text" class="form-control" id="groupName" name="nameGroup" required>
                </div>
                <button type="submit" class="btn btn-success">Добавить</button>
            </form>
        </div>
    </div>

    <h5 class="table-header">Список групп:</h5>
    <table class="table table-striped w-50">
        <thead>
        <tr>
            <th>Группа</th>
            <th></th>
            <th></th>
        </tr>
        </thead>
        <tbody class="table-group-divider">
        <#list groups as group>
            <tr>
                <td>${group.nameGroup}</td>
                <td>
                    <a href="/group/${group.id}">Изменить</a>
                </td>
                <td>
                    <form action="/group/delete" method="post">
                        <input type="hidden" value="${group.id}" name="groupId">
                        <button class="btn btn-primary" type="submit">Удалить</button>
                    </form>
                </td>
            </tr>
        </#list>
        </tbody>
    </table>

</@c.page>