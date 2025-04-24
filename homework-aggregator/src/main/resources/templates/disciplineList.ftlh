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
            <h5>Добавить новую дисциплину</h5>
        </div>
        <div class="card-body">
            <form action="/discipline/add" method="post">
                <div class="mb-3">
                    <label for="disciplineName" class="form-label">Введите название дисциплины:</label>
                    <input type="text" class="form-control" id="disciplineName" name="nameDiscipline" required>
                </div>
                <button type="submit" class="btn btn-success">Добавить</button>
            </form>
        </div>
    </div>

    <h5 class="table-header">Список дисциплин:</h5>
    <table class="table table-striped w-50">
        <thead>
        <tr>
            <th>Дисциплина</th>
            <th></th>
        </tr>
        </thead>
        <tbody class="table-group-divider">
        <#list disciplines as discipline>
            <tr>
                <td>${discipline.nameDiscipline}</td>
                <td>
                    <form action="/discipline/delete" method="post">
                        <input type="hidden" value="${discipline.id}" name="disciplineId">
                        <button class="btn btn-primary" type="submit">Удалить</button>
                    </form>
                </td>
            </tr>
        </#list>
        </tbody>
    </table>

</@c.page>