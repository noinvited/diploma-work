<#import "parts/commonTmpl.ftlh" as c>
<#include "parts/securityHlpr.ftlh">

<@c.page>
    <#if isStudent == 0>
        <h4>Для дальнейшей работы заполните данные о профиле и дождитесь получения статуса от администратора сайта.</h4>
    <#else>
        <div class="card">
            <div class="card-header">
                <div class="d-flex justify-content-between align-items-center">
                    <h5 class="card-title mb-0">Расписание группы ${selectedGroup.nameGroup}</h5>
                    <div class="btn-group">
                        <a href="/studentSchedule?weekShift=${weekShift - 1}" class="btn btn-outline-primary">
                            <i class="bi bi-chevron-left"></i> Предыдущая неделя
                        </a>
                        <a href="/studentSchedule?weekShift=0" class="btn btn-outline-primary <#if weekShift == 0>active</#if>">
                            Текущая неделя
                        </a>
                        <a href="/studentSchedule?weekShift=${weekShift + 1}" class="btn btn-outline-primary">
                            Следующая неделя <i class="bi bi-chevron-right"></i>
                        </a>
                    </div>
                </div>
            </div>
            <div class="card-body">
                <div class="table-responsive">
                    <table class="table table-bordered">
                        <thead>
                            <tr>
                                <th class="text-center">Время</th>
                                <#list days as day>
                                    <th class="text-center">${day}</th>
                                </#list>
                            </tr>
                        </thead>
                        <tbody>
                            <#list pairTimes as pair>
                                <tr>
                                    <td class="align-middle">
                                        ${pair.start?replace("Z", "")?replace("T", " ")?datetime("yyyy-MM-dd HH:mm:ss")?string("HH:mm")} - 
                                        ${pair.finish?replace("Z", "")?replace("T", " ")?datetime("yyyy-MM-dd HH:mm:ss")?string("HH:mm")}
                                    </td>
                                    <#list dates as date>
                                        <td class="align-middle position-relative" style="min-width: 200px;">
                                            <#assign lessonKey = date?replace("Z", "")?replace("T", " ")?datetime("yyyy-MM-dd")?string('yyyy-MM-dd') + "_" + pair.id>
                                            <#if lessons[lessonKey]??>
                                                <#assign lesson = lessons[lessonKey]>
                                                <div class="card">
                                                    <div class="card-body p-2">
                                                        <h6 class="card-title mb-1">${lesson.discipline.nameDiscipline}</h6>
                                                        <p class="card-text mb-1">
                                                            <small>
                                                                <i class="bi bi-person"></i> ${lesson.teacher.user.fio!lesson.teacher.user.username}<br>
                                                                <i class="bi bi-geo-alt"></i> ${lesson.classroom!''}<br>
                                                                <#if lesson.lessonType??><i class="bi bi-journal-text"></i> ${lesson.lessonType.type}</#if>
                                                            </small>
                                                        </p>
                                                    </div>
                                                </div>
                                            </#if>
                                        </td>
                                    </#list>
                                </tr>
                            </#list>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </#if>
</@c.page>