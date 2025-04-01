<#include  "securityHlpr.ftlh">

<#macro login path isRegisterForm>
    <form action="${path}" method="post">
        <div class="row mb-3">
            <label class="col-sm-2 col-form-label"> Логин: </label>
            <div class="col-sm-4">
                <input type="text" name="username" value="<#if user??>${user.username}</#if>"
                       class="form-control ${(usernameError??)?string('is-invalid', '')}"/>
                <#if usernameError??>
                    <div class="invalid-feedback">
                        ${usernameError}
                    </div>
                </#if>
            </div>
        </div>
        <div class="row mb-3">
            <label class="col-sm-2 col-form-label"> Пароль: </label>
            <div class="col-sm-4">
                <input type="password" name="password"
                       class="form-control ${(passwordError??)?string('is-invalid', '')}"/>
                <#if passwordError??>
                    <div class="invalid-feedback">
                        ${passwordError}
                    </div>
                </#if>
            </div>
        </div>
        <#if isRegisterForm>
            <div class="row mb-3">
                <label class="col-sm-2 col-form-label"> Повторите пароль: </label>
                <div class="col-sm-4">
                    <input type="password" name="password2"
                           class="form-control ${(password2Error??)?string('is-invalid', '')}"/>
                    <#if password2Error??>
                        <div class="invalid-feedback">
                            ${password2Error}
                        </div>
                    </#if>
                </div>
            </div>
            <div class="row mb-3">
                <label class="col-sm-2 col-form-label"> Почта: </label>
                <div class="col-sm-4">
                    <input type="email" name="email" value="<#if user??>${user.email}</#if>"
                           class="form-control ${(emailError??)?string('is-invalid', '')}"/>
                    <#if emailError??>
                        <div class="invalid-feedback">
                            ${emailError}
                        </div>
                    </#if>
                </div>
            </div>
            <div class="col-sm-4">
                <div class="g-recaptcha " data-sitekey="6LdJyAMrAAAAAIc1zsUM7qL6dS2qmsQ-q3tQp5IO"></div>
                <#if captchaError??>
                    <div class="alert alert-danger mt-3" role="alert">
                        ${captchaError}
                    </div>
                </#if>
            </div>
        </#if>
        <#if !isRegisterForm><div><a href="/registration">Регистрация</a></div></#if>
        <button type="submit" class="btn btn-primary mt-3"><#if isRegisterForm>Зарегистрироваться<#else>Вход</#if></button>
    </form>
</#macro>

<#macro logout>
    <form action="/logout" method="post">
        <button type="submit" class="btn btn-primary">
            <#if user??> Выход <#else> Вход </#if>
        </button>
    </form>
</#macro>