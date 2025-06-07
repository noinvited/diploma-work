<#import "loginTmpl.ftlh" as l>
<#include  "securityHlpr.ftlh">

<nav class="navbar navbar-expand-lg bg-body-tertiary">
    <div class="container-fluid">
        <a class="navbar-brand" href="/">HW aggregator</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarSupportedContent">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item">
                    <a class="nav-link" href="/">Главная страница</a>
                </li>
                <#if known>
                    <#if !isAdmin>
                        <li class="nav-item">
                            <a class="nav-link" <#if isStudent == 1> href="/studentSchedule" <#else> href="/teacherSchedule"</#if>>Расписание</a>
                        </li>
                        <#if isStudent != 0>
                            <#if isStudent == 1>
                                <li class="nav-item">
                                    <a class="nav-link" href="/marks">Оценки</a>
                                </li>
                            <#else>
                                <#if isStudent == 2>
                                    <li class="nav-item">
                                        <a class="nav-link" href="/groups">Мои группы</a>
                                    </li>
                                </#if>
                            </#if>
                            <li class="nav-item">
                                <a class="nav-link" <#if isStudent == 1> href="/studentTasks" <#else> href="/teacherTasks"</#if>> Мои Задания</a>
                            </li>
                        </#if>
                    <#else>
                        <li class="nav-item">
                            <a class="nav-link" href="/editSchedule">Составить расписание</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="/editGroup">Настройка групп</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="/editDiscipline">Настройка дисциплин</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="/user">Список пользователей</a>
                        </li>
                    </#if>
                    <li class="nav-item">
                        <a class="nav-link" href="/user/profile">Настройки профиля</a>
                    </li>
                </#if>
            </ul>
        </div>

        <div class="navbar-text me-3"><#if user??>${name}</#if></div>
        <@l.logout/>

    </div>
</nav>