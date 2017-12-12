<#import "/spring.ftl" as spring>
<html>
<h1>My products</h1>

Hello ${principal.getName()} (${accesstoken.getEmail()})

<ul>
<#list products as product>
    <li>${product}</li>
</#list>
</ul>
<br>
<a href="/logout">Logout</a>
</html>