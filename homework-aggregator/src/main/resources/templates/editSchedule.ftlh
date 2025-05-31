<#import "parts/commonTmpl.ftlh" as c>

<@c.page>
    <a class="btn btn-outline-primary mb-3" href="/">
        Вернуться в главное меню
    </a>
    
    <div class="row mb-3">
        <div class="col-md-6">
        <h6 class="mt-1">Выберите группу для составления расписания:</h6>
            <form method="get" action="/editSchedule" class="form-inline">
                <div class="input-group">
                    <select class="form-select" name="selectedGroup" id="selectedGroup">
                        <option value="">Выберите группу</option>
                        <#list groups as group>
                            <option value="${group.id}" <#if selectedGroup?? && selectedGroup.id == group.id>selected</#if>>
                                ${group.nameGroup}
                            </option>
                        </#list>
                    </select>
                    <button type="submit" class="btn btn-primary">Показать расписание</button>
                </div>
            </form>
        </div>
    </div>

    <#if selectedGroup??>
        <div class="card">
            <div class="card-header">
                <div class="d-flex justify-content-between align-items-center">
                    <h5 class="card-title mb-0">Расписание группы ${selectedGroup.nameGroup}</h5>
                    <div class="btn-group">
                        <a href="/editSchedule?selectedGroup=${selectedGroup.id}&weekShift=${weekShift - 1}" class="btn btn-outline-primary">
                            <i class="bi bi-chevron-left"></i> Предыдущая неделя
                        </a>
                        <a href="/editSchedule?selectedGroup=${selectedGroup.id}&weekShift=0" class="btn btn-outline-primary <#if weekShift == 0>active</#if>">
                            Текущая неделя
                        </a>
                        <a href="/editSchedule?selectedGroup=${selectedGroup.id}&weekShift=${weekShift + 1}" class="btn btn-outline-primary">
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
                                                        <div class="btn-group btn-group-sm">
                                                            <button type="button" class="btn btn-outline-primary"
                                                                    data-bs-toggle="modal" 
                                                                    data-bs-target="#editLessonModal"
                                                                    data-lesson-id="${lesson.id}"
                                                                    data-day="${date}"
                                                                    data-pair="${pair.id}"
                                                                    data-discipline-id="${lesson.discipline.id}"
                                                                    data-teacher-id="${lesson.teacher.id}"
                                                                    data-classroom="${lesson.classroom!''}"
                                                                    data-lesson-type-id="${(lesson.lessonType.id)!}">
                                                                <i class="bi bi-pencil"></i> Редактировать
                                                            </button>
                                                            <button type="button" class="btn btn-outline-danger"
                                                                    onclick="deleteLesson(${lesson.id})">
                                                                <i class="bi bi-trash"></i> Удалить
                                                            </button>
                                                        </div>
                                                    </div>
                                                </div>
                                            <#else>
                                                <button type="button" 
                                                        class="btn btn-outline-primary btn-sm position-absolute top-50 start-50 translate-middle" 
                                                        data-bs-toggle="modal" 
                                                        data-bs-target="#addLessonModal"
                                                        data-day="${date}"
                                                        data-pair="${pair.id}">
                                                    <i class="bi bi-plus-lg"></i> Добавить пару
                                                </button>
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

        <!-- Модальное окно для добавления пары -->
        <div class="modal fade" id="addLessonModal" tabindex="-1" aria-labelledby="addLessonModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="addLessonModalLabel">Добавить пару</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <form id="addLessonForm">
                            <input type="hidden" id="dayOfWeek" name="dayOfWeek">
                            <input type="hidden" id="pairNumber" name="pairNumber">
                            
                            <div class="mb-3">
                                <label for="discipline" class="form-label">Дисциплина</label>
                                <select class="form-select" id="discipline" name="discipline" required>
                                    <option value="">Выберите дисциплину</option>
                                    <#list disciplines as discipline>
                                        <option value="${discipline.id}">${discipline.nameDiscipline}</option>
                                    </#list>
                                </select>
                            </div>
                            
                            <div class="mb-3">
                                <label for="teacher" class="form-label">Преподаватель</label>
                                <select class="form-select" id="teacher" name="teacher" required disabled>
                                    <option value="">Сначала выберите дисциплину</option>
                                </select>
                            </div>
                            
                            <div class="mb-3">
                                <label for="classroom" class="form-label">Аудитория</label>
                                <input type="text" class="form-control" id="classroom" name="classroom" required>
                            </div>

                            <div class="mb-3">
                                <label for="lessonType" class="form-label">Тип занятия</label>
                                <select class="form-select" id="lessonType" name="lessonType" required>
                                    <option value="">Выберите тип занятия</option>
                                    <#list lessonTypes as type>
                                        <option value="${type.id}">${type.type}</option>
                                    </#list>
                                </select>
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Отмена</button>
                        <button type="button" class="btn btn-primary" onclick="saveLessonToSchedule()">Сохранить</button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Модальное окно для редактирования пары -->
        <div class="modal fade" id="editLessonModal" tabindex="-1" aria-labelledby="editLessonModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="editLessonModalLabel">Редактировать пару</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <form id="editLessonForm">
                            <input type="hidden" id="editLessonId" name="lessonId">
                            <input type="hidden" id="editDayOfWeek" name="dayOfWeek">
                            <input type="hidden" id="editPairNumber" name="pairNumber">
                            
                            <div class="mb-3">
                                <label for="editDiscipline" class="form-label">Дисциплина</label>
                                <select class="form-select" id="editDiscipline" name="discipline" required>
                                    <option value="">Выберите дисциплину</option>
                                    <#list disciplines as discipline>
                                        <option value="${discipline.id}">${discipline.nameDiscipline}</option>
                                    </#list>
                                </select>
                            </div>
                            
                            <div class="mb-3">
                                <label for="editTeacher" class="form-label">Преподаватель</label>
                                <select class="form-select" id="editTeacher" name="teacher" required>
                                    <option value="">Выберите преподавателя</option>
                                </select>
                            </div>
                            
                            <div class="mb-3">
                                <label for="editClassroom" class="form-label">Аудитория</label>
                                <input type="text" class="form-control" id="editClassroom" name="classroom" required>
                            </div>

                            <div class="mb-3">
                                <label for="editLessonType" class="form-label">Тип занятия</label>
                                <select class="form-select" id="editLessonType" name="lessonType">
                                    <option value="">Выберите тип занятия</option>
                                    <#list lessonTypes as type>
                                        <option value="${type.id}">${type.type}</option>
                                    </#list>
                                </select>
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Отмена</button>
                        <button type="button" class="btn btn-primary" onclick="updateLesson()">Сохранить изменения</button>
                    </div>
                </div>
            </div>
        </div>

        <script>
            // Обработка открытия модального окна
            const addLessonModal = document.getElementById('addLessonModal');
            addLessonModal.addEventListener('show.bs.modal', function (event) {
                const button = event.relatedTarget;
                const day = button.getAttribute('data-day');
                const pair = button.getAttribute('data-pair');
                
                document.getElementById('dayOfWeek').value = day;
                document.getElementById('pairNumber').value = pair;

                // Сброс формы и ошибок
                clearValidationErrors();
                document.getElementById('discipline').value = '';
                const teacherSelect = document.getElementById('teacher');
                teacherSelect.value = '';
                teacherSelect.disabled = true;
                teacherSelect.innerHTML = '<option value="">Сначала выберите дисциплину</option>';
                document.getElementById('classroom').value = '';
                document.getElementById('lessonType').value = '';
            });

            // Обработка изменения дисциплины
            document.getElementById('discipline').addEventListener('change', function() {
                const disciplineId = this.value;
                const teacherSelect = document.getElementById('teacher');
                
                if (disciplineId) {
                    teacherSelect.disabled = true;
                    teacherSelect.innerHTML = '<option value="">Загрузка преподавателей...</option>';
                    
                    fetch('/discipline/' + disciplineId + '/teachers')
                        .then(response => response.json())
                        .then(teachers => {
                            teacherSelect.innerHTML = '<option value="">Выберите преподавателя</option>';
                            if (teachers && teachers.length > 0) {
                                teachers.forEach(teacher => {
                                    const option = document.createElement('option');
                                    option.value = teacher.id;
                                    option.textContent = teacher.user.fio || teacher.user.username;
                                    teacherSelect.appendChild(option);
                                });
                                teacherSelect.disabled = false;
                            } else {
                                teacherSelect.innerHTML = '<option value="">Нет доступных преподавателей</option>';
                                teacherSelect.disabled = true;
                            }
                        })
                        .catch(error => {
                            teacherSelect.innerHTML = '<option value="">Ошибка загрузки преподавателей</option>';
                            teacherSelect.disabled = true;
                        });
                } else {
                    teacherSelect.innerHTML = '<option value="">Сначала выберите дисциплину</option>';
                    teacherSelect.disabled = true;
                }
            });

            // Функция сохранения пары
            function saveLessonToSchedule() {
                const form = document.getElementById('addLessonForm');
                const discipline = document.getElementById('discipline');
                const teacher = document.getElementById('teacher');
                const classroom = document.getElementById('classroom');
                const lessonType = document.getElementById('lessonType');

                // Очистка предыдущих сообщений об ошибках
                clearValidationErrors();

                // Проверка обязательных полей
                let isValid = true;

                if (!discipline.value) {
                    showError(discipline, 'Выберите дисциплину');
                    isValid = false;
                }

                if (!teacher.value) {
                    showError(teacher, 'Выберите преподавателя');
                    isValid = false;
                }

                if (!classroom.value.trim()) {
                    showError(classroom, 'Укажите аудиторию');
                    isValid = false;
                }

                if (!lessonType.value) {
                    showError(lessonType, 'Выберите тип занятия');
                    isValid = false;
                }

                if (isValid && form.checkValidity()) {
                    const formData = new FormData(form);
                    const lessonData = {
                        date: formData.get('dayOfWeek'),
                        pairId: formData.get('pairNumber'),
                        groupId: ${selectedGroup.id},
                        disciplineId: formData.get('discipline'),
                        teacherId: formData.get('teacher'),
                        classroom: formData.get('classroom').trim(),
                        lessonTypeId: formData.get('lessonType')
                    };

                    fetch('/lessons', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                        },
                        body: JSON.stringify(lessonData)
                    })
                    .then(response => {
                        if (response.ok) {
                            const modal = bootstrap.Modal.getInstance(addLessonModal);
                            modal.hide();
                            window.location.reload();
                        } else {
                            throw new Error('Не удалось сохранить пару');
                        }
                    })
                    .catch(error => {
                        showModalError('Произошла ошибка при сохранении пары');
                    });
                }
            }

            // Вспомогательные функции для валидации
            function showError(element, message) {
                const errorDiv = document.createElement('div');
                errorDiv.className = 'invalid-feedback';
                errorDiv.textContent = message;
                element.classList.add('is-invalid');
                element.parentElement.appendChild(errorDiv);
            }

            function clearValidationErrors() {
                const form = document.getElementById('addLessonForm');
                form.querySelectorAll('.is-invalid').forEach(element => {
                    element.classList.remove('is-invalid');
                });
                form.querySelectorAll('.invalid-feedback').forEach(element => {
                    element.remove();
                });
            }

            function showModalError(message) {
                const errorDiv = document.createElement('div');
                errorDiv.className = 'alert alert-danger mt-3';
                errorDiv.textContent = message;
                document.querySelector('#addLessonForm').prepend(errorDiv);
                setTimeout(() => errorDiv.remove(), 5000);
            }

            // Автоматическая отправка формы при выборе группы
            document.getElementById('selectedGroup').addEventListener('change', function() {
                this.form.submit();
            });

            // Обработка открытия модального окна редактирования
            const editLessonModal = document.getElementById('editLessonModal');
            editLessonModal.addEventListener('show.bs.modal', function (event) {
                const button = event.relatedTarget;
                
                // Заполняем скрытые поля
                document.getElementById('editLessonId').value = button.getAttribute('data-lesson-id');
                document.getElementById('editDayOfWeek').value = button.getAttribute('data-day');
                document.getElementById('editPairNumber').value = button.getAttribute('data-pair');
                
                // Заполняем выбор дисциплины
                const disciplineId = button.getAttribute('data-discipline-id');
                const disciplineSelect = document.getElementById('editDiscipline');
                disciplineSelect.value = disciplineId;
                
                // Загружаем преподавателей для выбранной дисциплины
                const teacherId = button.getAttribute('data-teacher-id');
                if (disciplineId) {
                    fetch('/discipline/' + disciplineId + '/teachers')
                        .then(response => response.json())
                        .then(teachers => {
                            const teacherSelect = document.getElementById('editTeacher');
                            teacherSelect.innerHTML = '<option value="">Выберите преподавателя</option>';
                            if (teachers && teachers.length > 0) {
                                teachers.forEach(teacher => {
                                    const option = document.createElement('option');
                                    option.value = teacher.id;
                                    option.textContent = teacher.user.fio || teacher.user.username;
                                    teacherSelect.appendChild(option);
                                });
                                teacherSelect.value = teacherId;
                                teacherSelect.disabled = false;
                            }
                        });
                }
                
                // Заполняем аудиторию
                document.getElementById('editClassroom').value = button.getAttribute('data-classroom');
                
                // Заполняем тип занятия
                const lessonTypeId = button.getAttribute('data-lesson-type-id');
                if (lessonTypeId) {
                    document.getElementById('editLessonType').value = lessonTypeId;
                }
            });

            // Обработчик изменения дисциплины в форме редактирования
            document.getElementById('editDiscipline').addEventListener('change', function() {
                const disciplineId = this.value;
                const teacherSelect = document.getElementById('editTeacher');
                
                if (disciplineId) {
                    teacherSelect.disabled = true;
                    teacherSelect.innerHTML = '<option value="">Загрузка преподавателей...</option>';
                    
                    fetch('/discipline/' + disciplineId + '/teachers')
                        .then(response => response.json())
                        .then(teachers => {
                            teacherSelect.innerHTML = '<option value="">Выберите преподавателя</option>';
                            if (teachers && teachers.length > 0) {
                                teachers.forEach(teacher => {
                                    const option = document.createElement('option');
                                    option.value = teacher.id;
                                    option.textContent = teacher.user.fio || teacher.user.username;
                                    teacherSelect.appendChild(option);
                                });
                                teacherSelect.disabled = false;
                            } else {
                                teacherSelect.innerHTML = '<option value="">Нет доступных преподавателей</option>';
                                teacherSelect.disabled = true;
                            }
                        })
                        .catch(error => {
                            teacherSelect.innerHTML = '<option value="">Ошибка загрузки преподавателей</option>';
                            teacherSelect.disabled = true;
                        });
                } else {
                    teacherSelect.innerHTML = '<option value="">Сначала выберите дисциплину</option>';
                    teacherSelect.disabled = true;
                }
            });

            // Функция обновления пары
            function updateLesson() {
                const form = document.getElementById('editLessonForm');
                const discipline = document.getElementById('editDiscipline');
                const teacher = document.getElementById('editTeacher');
                const classroom = document.getElementById('editClassroom');
                const lessonType = document.getElementById('editLessonType');
                const lessonId = document.getElementById('editLessonId');

                // Очистка предыдущих сообщений об ошибках
                clearEditValidationErrors();

                // Проверка обязательных полей
                let isValid = true;

                if (!discipline.value) {
                    showEditError(discipline, 'Выберите дисциплину');
                    isValid = false;
                }

                if (!teacher.value) {
                    showEditError(teacher, 'Выберите преподавателя');
                    isValid = false;
                }

                if (!classroom.value.trim()) {
                    showEditError(classroom, 'Укажите аудиторию');
                    isValid = false;
                }

                if (!lessonType.value) {
                    showEditError(lessonType, 'Выберите тип занятия');
                    isValid = false;
                }

                if (isValid && form.checkValidity()) {
                    const formData = new FormData(form);
                    const lessonData = {
                        id: lessonId.value,
                        date: formData.get('dayOfWeek'),
                        pairId: formData.get('pairNumber'),
                        groupId: ${selectedGroup.id},
                        disciplineId: formData.get('discipline'),
                        teacherId: formData.get('teacher'),
                        classroom: formData.get('classroom').trim(),
                        lessonTypeId: formData.get('lessonType')
                    };

                    fetch('/lessons/' + lessonId.value, {
                        method: 'PUT',
                        headers: {
                            'Content-Type': 'application/json',
                        },
                        body: JSON.stringify(lessonData)
                    })
                    .then(response => {
                        if (response.ok) {
                            const modal = bootstrap.Modal.getInstance(editLessonModal);
                            modal.hide();
                            window.location.reload();
                        } else {
                            throw new Error('Не удалось обновить пару');
                        }
                    })
                    .catch(error => {
                        showEditModalError('Произошла ошибка при обновлении пары');
                    });
                }
            }

            // Вспомогательные функции для валидации формы редактирования
            function showEditError(element, message) {
                const errorDiv = document.createElement('div');
                errorDiv.className = 'invalid-feedback';
                errorDiv.textContent = message;
                element.classList.add('is-invalid');
                element.parentElement.appendChild(errorDiv);
            }

            function clearEditValidationErrors() {
                const form = document.getElementById('editLessonForm');
                form.querySelectorAll('.is-invalid').forEach(element => {
                    element.classList.remove('is-invalid');
                });
                form.querySelectorAll('.invalid-feedback').forEach(element => {
                    element.remove();
                });
            }

            function showEditModalError(message) {
                const errorDiv = document.createElement('div');
                errorDiv.className = 'alert alert-danger mt-3';
                errorDiv.textContent = message;
                document.querySelector('#editLessonForm').prepend(errorDiv);
                setTimeout(() => errorDiv.remove(), 5000);
            }
        </script>
    </#if>
</@c.page>