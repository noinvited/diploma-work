<#import "parts/commonTmpl.ftlh" as c>
<#import "parts/loginTmpl.ftlh" as l>

<@c.page>
    <h5 class="m-3">Регистрация:</h5>
    ${message!}
    <@l.login "/registration" true/>
</@c.page>