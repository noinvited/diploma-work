<#assign
known = SPRING_SECURITY_CONTEXT??
>

<#if known>
    <#assign
    user = SPRING_SECURITY_CONTEXT.authentication.principal
    name = user.getUsername()
    isAdmin = user.isAdmin()
    isStudent = user.isStudent()
    currentUserId = user.getId()
    >
<#else>
    <#assign
    name = "unknown"
    isAdmin = false
    currentUserId = -1
    >
</#if>