<#import "parts/commonTmpl.ftlh" as c>
<#import "parts/loginTmpl.ftlh" as l>

<@c.page>
    <a class="btn btn-outline-primary mb-3" href="/login">
        Вернуться назад
    </a>

    <h5 class="m-3">Регистрация:</h5>
    ${message!}
    <@l.login "/registration" true/>
</@c.page>