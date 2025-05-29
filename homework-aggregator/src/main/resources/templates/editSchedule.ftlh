<#import "parts/commonTmpl.ftlh" as c>

<@c.page>
    <a class="btn btn-outline-primary mb-3" href="/">
        Вернуться в главное меню
    </a>

    <div class="form-group mb-3">
        <h6 class="mt-1">Выберите группу для составления расписания:</h6>
        <select class="form-select" id="groupSelector" name="groupSelector" style="max-width: 300px;" onchange="loadSchedule(this.value)">
            <option value="" <#if !selectedGroup??>selected</#if>>Выберите группу</option>
            <#list groups as group>
                <option value="${group.id}"
                        <#if selectedGroup?? && selectedGroup == group.id>selected</#if>>
                    ${group.nameGroup}
                </option>
            </#list>
        </select>
    </div>



    <script>
        function loadSchedule(groupId) {
            if (groupId) {
                window.location.href = '/editSchedule?selectedGroup=' + groupId;
            }
        }

        <#if selectedGroup??>
        document.addEventListener('DOMContentLoaded', function() {
            loadSchedule('${selectedGroup}');
        });
        </#if>
    </script>

</@c.page>