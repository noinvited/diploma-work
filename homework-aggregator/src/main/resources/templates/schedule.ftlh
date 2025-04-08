<#import "parts/commonTmpl.ftlh" as c>
<#include "parts/securityHlpr.ftlh">

<@c.page>
    <#if isStudent == 0>
        <h4>Для дальнейшей работы заполните данные о профиле и дождитесь получения статуса от администратора сайта.</h4>
    <#else>
        <#if isStudent == 1>
            Расписание для студента
        <#else>
            Расписание для преподавателя
        </#if>
    </#if>
</@c.page>