<#import "parts/commonTmpl.ftlh" as c>
<#import "parts/loginTmpl.ftlh" as l>

<@c.page>
    <h5 class="m-3">Вход на страницу:</h5>
    <#if SPRING_SECURITY_LAST_EXCEPTION??>
        <div class="alert alert-danger" role="alert">
            ${SPRING_SECURITY_LAST_EXCEPTION.message}
        </div>
    </#if>
    <#if message??>
        <div class="alert alert-${messageType}" role="alert">
            ${message}
        </div>
    </#if>
    <@l.login "/login" false/>
</@c.page>