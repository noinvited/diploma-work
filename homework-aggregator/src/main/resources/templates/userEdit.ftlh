<#import "parts/commonTmpl.ftlh" as c>

<@c.page>
    <a class="btn btn-outline-primary mb-3" href="/user">
        Вернуться назад
    </a>

    <#if message1??>
        <div class="alert alert-${messageType1!"info"} alert-dismissible fade show" role="alert">
            ${message1}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </#if>

    <div class="card card-body col-sm-6 mb-3">
        <form action="/user/{userId}" method="post">
            <input type="text" name="username" class="form-control" value="${user.username}" required>

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
                        <input type="radio" name="statusUser" class="form-check-input"
                               value="STUDENT" <#if user.status?? && user.status == 'STUDENT'>checked</#if>>
                        <label class="form-check-label">
                            Студент
                        </label>
                    </div>
                    <div class="form-check">
                        <input type="radio" name="statusUser" class="form-check-input"
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
                           value="<#if studentTicket??>${studentTicket?c}</#if>">
                </div>

                <div class="form-group mb-3">
                    <h6 class="mt-1">Группа:</h6>
                    <select class="form-select" id="group" name="group" style="max-width: 300px;">
                        <option value="" disabled <#if !studentGroup??>selected</#if>>Выберите группу</option>
                        <#list groups as group>
                            <option value="${(group.id)!''}"
                                    <#if studentGroup?? && group.id?? && studentGroup == group.id>selected</#if>>
                                ${(group.nameGroup)!''}
                            </option>
                        </#list>
                    </select>
                </div>
            </div>

            <div id="teacherForm" class="mt-3 <#if !(user.status?? && user.status == 'TEACHER')>d-none</#if>">
                <div class="form-group mb-1">
                    <h6 class="mt-1">Дисциплины:</h6>
                    <div class="discipline-checkboxes">
                        <#list disciplines as discipline>
                            <div class="form-check">
                                <input class="form-check-input discipline-checkbox" type="checkbox"
                                       name="disciplines" value="${discipline.id}"
                                       id="discipline-${discipline.id}"
                                       <#if teacherDisciplines?? && teacherDisciplines?seq_contains(discipline)>checked</#if>>
                                <label class="form-check-label" for="discipline-${discipline.id}">
                                    ${discipline.nameDiscipline}
                                </label>
                            </div>
                        </#list>
                    </div>
                </div>

                <div class="mt-2 mb-1">
                    <button type="button" id="confirmDisciplines" class="btn btn-outline-secondary">
                        Подтвердить выбор дисциплин
                    </button>

                    <div id="loadingIndicator" class="spinner-border spinner-border-sm d-none" role="status">
                        <span class="visually-hidden">Загрузка...</span>
                    </div>
                </div>

                <div id="groupsContainer" class="form-group mb-3 <#if !teacherGroups??>d-none</#if>">
                    <h6 class="mt-1">Доступные группы:</h6>
                    <div id="groupsCheckboxes">
                        <#list groups as group>
                            <div class="form-check-available-groups">
                                <input class="form-check-input group-checkbox" type="checkbox"
                                       name="teacherGroups" value="${(group.id)!''}"
                                       id="group-${(group.id)!''}"
                                       <#if teacherGroups?? && group.id?? && teacherGroups?seq_contains(group.id)>checked</#if>>
                                <label class="form-check-label ms-2" for="group-${(group.id)!''}">
                                    ${(group.nameGroup)!''}
                                </label>
                            </div>
                        </#list>
                    </div>
                </div>
            </div>

            <script>
                document.addEventListener('DOMContentLoaded', function () {
                    const roleRadios = document.querySelectorAll('input[name="role"]');
                    const statusRadios = document.querySelectorAll('input[name="statusUser"]');
                    const userStatusForm = document.getElementById('userStatusForm');
                    const studentForm = document.getElementById('studentForm');
                    const teacherForm = document.getElementById('teacherForm');
                    const groupsContainer = document.getElementById('groupsContainer');
                    const loadingIndicator = document.getElementById('loadingIndicator');

                    // Handle role selection
                    roleRadios.forEach(radio => {
                        radio.addEventListener('change', function() {
                            if (this.value === 'USER') {
                                userStatusForm.classList.remove('d-none');
                                studentForm.classList.add('d-none');
                                teacherForm.classList.add('d-none');
                            } else {
                                userStatusForm.classList.add('d-none');
                                studentForm.classList.add('d-none');
                                teacherForm.classList.add('d-none');
                                statusRadios.forEach(statusRadio => {
                                    statusRadio.checked = false;
                                });
                            }
                        });
                    });

                    // Handle status selection (student/teacher)
                    statusRadios.forEach(radio => {
                        radio.addEventListener('change', function() {
                            if (this.value === 'STUDENT') {
                                studentForm.classList.remove('d-none');
                                teacherForm.classList.add('d-none');
                            } else if (this.value === 'TEACHER') {
                                teacherForm.classList.remove('d-none');
                                studentForm.classList.add('d-none');
                                groupsContainer.classList.add('d-none');
                            }
                        });
                    });

                    // Handle discipline confirmation and show groups
                    document.getElementById('confirmDisciplines').addEventListener('click', function() {
                        var selectedDisciplines = Array.from(
                            document.querySelectorAll('.discipline-checkbox:checked')
                        ).map(function(checkbox) {
                            return checkbox.value;
                        });

                        if (selectedDisciplines.length === 0) {
                            alert('Пожалуйста, выберите хотя бы одну дисциплину');
                            return;
                        }

                        var confirmButton = this;
                        confirmButton.disabled = true;
                        loadingIndicator.classList.remove('d-none');

                        // Get user ID from the hidden input
                        var userId = document.querySelector('input[name="userId"]').value;

                        // Create URL with parameters in more compatible way
                        var queryString = '';
                        for (var i = 0; i < selectedDisciplines.length; i++) {
                            if (i > 0) queryString += '&';
                            queryString += 'disciplineIds=' + selectedDisciplines[i];
                        }
                        var url = '/user/' + userId + '/getGroupsByDisciplines?' + queryString;

                        // Fetch groups that have selected disciplines
                        fetch(url)
                            .then(function(response) {
                                if (!response.ok) {
                                    throw new Error('Network response was not ok');
                                }
                                return response.json();
                            })
                            .then(function(groups) {
                                var groupsCheckboxes = document.getElementById('groupsCheckboxes');
                                groupsCheckboxes.innerHTML = '';

                                if (groups && groups.length > 0) {
                                    groups.forEach(function(group) {
                                        var div = document.createElement('div');
                                        div.className = 'form-check-available-groups';
                                        div.innerHTML = 
                                            '<input class="form-check-input group-checkbox" type="checkbox" ' +
                                            'name="teacherGroups" value="' + group.id + '" ' +
                                            'id="group-' + group.id + '">' +
                                            '<label class="form-check-label ms-2" for="group-' + group.id + '">' +
                                            group.nameGroup +
                                            '</label>';
                                        groupsCheckboxes.appendChild(div);
                                    });
                                } else {
                                    groupsCheckboxes.innerHTML = '<div class="alert alert-info">Нет групп с выбранными дисциплинами</div>';
                                }
                                groupsContainer.classList.remove('d-none');
                            })
                            .catch(function(error) {
                                console.error('Error:', error);
                                alert('Произошла ошибка при загрузке групп');
                            })
                            .finally(function() {
                                confirmButton.disabled = false;
                                loadingIndicator.classList.add('d-none');
                            });
                    });

                    // Initial form state setup
                    const initialRole = document.querySelector('input[name="role"]:checked');
                    if (initialRole && initialRole.value === 'USER') {
                        userStatusForm.classList.remove('d-none');
                        const initialStatus = document.querySelector('input[name="statusUser"]:checked');
                        if (initialStatus) {
                            if (initialStatus.value === 'STUDENT') {
                                studentForm.classList.remove('d-none');
                            } else if (initialStatus.value === 'TEACHER') {
                                teacherForm.classList.remove('d-none');
                            }
                        }
                    }
                });
            </script>

            <input type="hidden" value="${user.id}" name="userId">
            <button type="submit" class="btn btn-primary mt-3">Сохранить</button>
        </form>
    </div>
</@c.page>