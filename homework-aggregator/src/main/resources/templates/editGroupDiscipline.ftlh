<#import "parts/commonTmpl.ftlh" as c>

<@c.page>
    <a class="btn btn-outline-primary mb-3"  href="/">
        Вернуться в главное меню
    </a>

    <div class="card card-body col-sm-6">
        <form action="/editGroupDiscipline" method="post">
            <h6 class="mt-1">Выберите группу из списка:</h6>

            <button type="submit" class="btn btn-primary mt-3">Сохранить</button>
        </form>
    </div>
</@c.page>