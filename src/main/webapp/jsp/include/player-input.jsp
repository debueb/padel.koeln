<%@include file="/jsp/include/include.jsp"%>
<fmt:message key="FirstName" var="FirstName"/>
<fmt:message key="LastName" var="LastName"/>
<fmt:message key="EmailAddress" var="EmailAddress"/>
<fmt:message key="PhoneNumber" var="PhoneNumber"/>
<fmt:message key="Password" var="Password"/>
<div class="relative">
    <spf:input path="firstName" type="text" class="form-control form-top-element"/>
    <div class="explanation">${FirstName}</div>
</div>
<div class="relative">
    <spf:input path="lastName" type="text" class="form-control form-center-element"/>
    <div class="explanation">${LastName}</div>
</div>
<div class="relative">
    <spf:input path="email" type="email"  class="form-control form-center-element"/>
    <div class="explanation">${EmailAddress}</div>
</div>
<div class="relative">
    <spf:input path="phone" type="tel"  class="form-control ${not empty param.showPassword ? 'form-center-element' : 'form-bottom-element'}"/>
    <div class="explanation">${PhoneNumber}</div>
</div>
<c:if test="${not empty param.showPassword}">
    <div class="relative">
        <spf:input path="password" type="password"  class="form-control form-bottom-element"/>
        <div class="explanation">${Password}</div>
    </div>
</c:if>
            