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
                                    <#list 1..6 as day>
                                        <td class="align-middle position-relative" style="min-width: 200px;">
                                            <button type="button" 
                                                    class="btn btn-outline-primary btn-sm position-absolute top-50 start-50 translate-middle" 
                                                    data-bs-toggle="modal" 
                                                    data-bs-target="#addLessonModal"
                                                    data-day="${day}"
                                                    data-pair="${pair.id}">
                                                <i class="bi bi-plus-lg"></i> Добавить пару
                                            </button>
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
                                <select class="form-select" id="teacher" name="teacher" required>
                                    <option value="">Выберите преподавателя</option>
                                </select>
                            </div>
                            
                            <div class="mb-3">
                                <label for="classroom" class="form-label">Аудитория</label>
                                <input type="text" class="form-control" id="classroom" name="classroom" required>
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

        <script>
            // Обработка открытия модального окна
            const addLessonModal = document.getElementById('addLessonModal');
            addLessonModal.addEventListener('show.bs.modal', function (event) {
                const button = event.relatedTarget;
                const day = button.getAttribute('data-day');
                const pair = button.getAttribute('data-pair');
                
                document.getElementById('dayOfWeek').value = day;
                document.getElementById('pairNumber').value = pair;
            });

            // Обработка изменения дисциплины
            document.getElementById('discipline').addEventListener('change', function() {
                const disciplineId = this.value;
                if (disciplineId) {
                    // Здесь можно добавить AJAX запрос для получения преподавателей по выбранной дисциплине
                    // и заполнить select с id="teacher"
                }
            });

            // Функция сохранения пары
            function saveLessonToSchedule() {
                const form = document.getElementById('addLessonForm');
                if (form.checkValidity()) {
                    // Здесь можно добавить AJAX запрос для сохранения пары
                    const modal = bootstrap.Modal.getInstance(addLessonModal);
                    modal.hide();
                } else {
                    form.reportValidity();
                }
            }

            // Автоматическая отправка формы при выборе группы
            document.getElementById('selectedGroup').addEventListener('change', function() {
                this.form.submit();
            });
        </script>
    </#if>










</@c.page>