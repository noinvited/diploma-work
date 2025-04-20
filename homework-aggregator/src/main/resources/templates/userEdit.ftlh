<#import "parts/commonTmpl.ftlh" as c>

<@c.page>
    <a class="btn btn-outline-primary mb-3" href="/user">
        Вернуться назад
    </a>

    <div class="card card-body col-sm-6 mb-3">
        <form action="/user" method="post">
            <input type="text" name="username" class="form-control" value="${user.username}">

            <h6 class="mt-1">Статус учетной записи:</h6>
            <div class="form-check form-switch mt-1">
                <input name="status" class="form-check-input" type="checkbox" role="switch" id="flexSwitchCheck"
                       <#if user.active>checked</#if>>
                <label class="form-check-label" for="flexSwitchCheck">Заблокирована/Разблокирована</label>
            </div>

            <h6 class="mt-1">Роль пользователя:</h6>
            <div class="form-group mt-1">
                <div class="form-check">
                    <input type="radio" name="role" class="form-check-input" id="userRole"
                           value="USER" <#if user.role == 'USER'>checked</#if>>
                    <label class="form-check-label">
                        Пользователь
                    </label>
                </div>
                <div class="form-check">
                    <input type="radio" name="role" class="form-check-input" id="adminRole"
                           value="ADMIN" <#if user.role == 'ADMIN'>checked</#if>>
                    <label class="form-check-label">
                        Администратор
                    </label>
                </div>
            </div>

            <div id="userStatusForm" class="mt-1 <#if user.role != 'USER'>d-none</#if>">
                <h6 class="mt-1">Статус пользователя:</h6>
                <div class="form-group">
                    <div class="form-check">
                        <input type="radio" name="status" class="form-check-input"
                               value="STUDENT" <#if user.status?? && user.status == 'STUDENT'>checked</#if>>
                        <label class="form-check-label">
                            Студент
                        </label>
                    </div>
                    <div class="form-check">
                        <input type="radio" name="status" class="form-check-input"
                               value="TEACHER" <#if user.status?? && user.status == 'TEACHER'>checked</#if>>
                        <label class="form-check-label">
                            Преподаватель
                        </label>
                    </div>
                </div>
            </div>

            <div id="studentForm" class="mt-3 <#if !(user.status?? && user.status == 'STUDENT')>d-none</#if>">
                <div class="form-group mb-1">
                    <h6 class="mt-1">Номер студенческого билета:</h6>
                    <input type="text" class="form-control" id="studentId" name="studentId"
                           value="<#if studentTicket??>${studentTicket}</#if>">
                </div>

                <div class="form-group mb-3">
                    <h6 class="mt-1">Группа:</h6>
                    <select class="form-select" id="group" name="group" style="max-width: 300px;">
                        <option value="" disabled <#if !studentGroup??>selected</#if>>Выберите группу</option>
                        <#list groups as group>
                            <option value="${group.id}"
                                    <#if studentGroup?? && studentGroup == group.id>selected</#if>>
                                ${group.nameGroup}
                            </option>
                        </#list>
                    </select>
                </div>
            </div>

            <div id="teacherForm" class="mt-3 <#if !(user.status?? && user.status == 'TEACHER')>d-none</#if>">
                <div class="form-group mb-1">
                    <h6 class="mt-1">Дисциплины:</h6>
                    <select class="form-select" id="disciplines" name="disciplines" multiple size="10">
                        <#list disciplines as discipline>
                            <option value="${discipline.id}"
                                    <#if teacherDisciplines?? && teacherDisciplines?seq_contains(discipline.id)>selected</#if>>
                                ${discipline.nameDiscipline}
                            </option>
                        </#list>
                    </select>
                    <small class="text-muted">Удерживайте Ctrl (Cmd на Mac) для выбора нескольких</small>
                </div>

                <div class="form-group mb-3">
                    <h6 class="mt-1">Группы:</h6>
                    <select class="form-select" id="teacherGroups" name="teacherGroups" multiple size="10">
                        <#list groups as group>
                            <option value="${group.id}"
                                    <#if teacherGroups?? && teacherGroups?seq_contains(group.id)>selected</#if>>
                                ${group.nameGroup}
                            </option>
                        </#list>
                    </select>
                    <small class="text-muted">Удерживайте Ctrl (Cmd на Mac) для выбора нескольких</small>
                </div>
            </div>


            <script>
                document.addEventListener('DOMContentLoaded', function () {
                    const roleRadios = document.querySelectorAll('input[name="role"]');
                    const statusRadios = document.querySelectorAll('input[name="status"]');
                    const statusForm = document.getElementById('userStatusForm');
                    const studentForm = document.getElementById('studentForm');
                    const teacherForm = document.getElementById('teacherForm');

                    roleRadios.forEach(radio => {
                        radio.addEventListener('change', function () {
                            if (this.value === 'USER' && this.checked) {
                                statusForm.classList.remove('d-none');
                            } else {
                                statusForm.classList.add('d-none');
                                studentForm.classList.add('d-none');
                                teacherForm.classList.add('d-none');
                            }
                        });
                    });

                    statusRadios.forEach(radio => {
                        radio.addEventListener('change', function () {
                            if (this.value === 'STUDENT' && this.checked) {
                                studentForm.classList.remove('d-none');
                                teacherForm.classList.add('d-none');
                            } else if (this.value === 'TEACHER' && this.checked) {
                                teacherForm.classList.remove('d-none');
                                studentForm.classList.add('d-none');
                            } else {
                                studentForm.classList.add('d-none');
                                teacherForm.classList.add('d-none');
                            }
                        });
                    });
                });
            </script>

            <input type="hidden" value="${user.id}" name="userId">
            <button type="submit" class="btn btn-primary mt-3">Сохранить</button>
        </form>
    </div>
</@c.page>