<#import "parts/commonTmpl.ftlh" as c>
<#include "parts/securityHlpr.ftlh">

<@c.page>
    <a class="btn btn-outline-primary mb-3" href="/schedule">
        Вернуться к расписанию
    </a>
    <h4 class="m-3">${username}</h4>
    <#if isStudent != 0>
        <div>Статус: ${status}</div>
        <#if isStudent == 1>
            <div>Группа: ${group}</div>
        </#if>
    </#if>
    <#if message1??>
        <div class="alert alert-${messageType1}" role="alert">
            ${message1}
        </div>
    </#if>
    <#if message2??>
        <div class="alert alert-${messageType2}" role="alert">
            ${message2}
        </div>
    </#if>
    <#if message3??>
        <div class="alert alert-${messageType3}" role="alert">
            ${message3}
        </div>
    </#if>
    <#if message4??>
        <div class="alert alert-${messageType4}" role="alert">
            ${message4}
        </div>
    </#if>
    <div class="card card-body">
        <form method="post">
            <h6 class="mb-3">Заполнение личных данных:</h6>
            <div class="row mb-3">
                <label class="col-sm-2 col-form-label"> Введите ФИО: </label>
                <div class="col-sm-4">
                    <input type="text" name="fio" class="form-control" value="${fio!}"/>
                </div>
            </div>
            <div class="row mb-3">
                <label class="col-sm-2 col-form-label"> Введите дату рождения: </label>
                <div class="col-sm-4">
                    <input type="date" name="birthdate" class="form-control" value="${birthdate!}"/>
                </div>
            </div>

            <h6 class="mb-3">Измененение пароля:</h6>
            <div class="row mb-3">
                <label class="col-sm-2 col-form-label"> Введите старый пароль: </label>
                <div class="col-sm-4">
                    <input type="password" name="oldPassword" class="form-control"/>
                </div>
            </div>
            <div class="row mb-3">
                <label class="col-sm-2 col-form-label"> Введите новый пароль: </label>
                <div class="col-sm-4">
                    <input type="password" name="newPassword" class="form-control"/>
                </div>
            </div>

            <h6 class="mb-3">Измененение почты:</h6>
            <div class="row mb-3">
                <label class="col-sm-1 col-form-label"> Почта: </label>
                <div class="col-sm-5">
                    <input type="email" name="email" class="form-control" value="${email!}"/>
                </div>
            </div>
            <button type="submit" class="btn btn-primary">Сохранить изменения</button>
        </form>
    </div>
</@c.page>