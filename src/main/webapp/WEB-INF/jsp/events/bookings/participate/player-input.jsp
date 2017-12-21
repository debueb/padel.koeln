<%@include file="/WEB-INF/jsp/include/include.jsp"%>
<fmt:message key="FirstName" var="FirstName"/>
<fmt:message key="LastName" var="LastName"/>
<fmt:message key="EmailAddress" var="EmailAddress"/>
<fmt:message key="PhoneNumber" var="PhoneNumber"/>
<fmt:message key="Password" var="Password"/>
<div class="relative">
    <spf:input path="player.firstName" type="text" class="form-control form-top-element"/>
    <div class="explanation">${FirstName}</div>
</div>
<div class="relative">
    <spf:input path="player.lastName" type="text" class="form-control form-center-element"/>
    <div class="explanation">${LastName}</div>
</div>
<div class="relative">
    <spf:input path="player.email" type="email"  class="form-control form-center-element"/>
    <div class="explanation">${EmailAddress}</div>
</div>
<div class="relative">
    <spf:input path="player.phone" type="tel"  class="form-control form-center-element"/>
    <div class="explanation">${PhoneNumber}</div>
</div>
<div class="relative">
    <spf:select path="player.gender" class="select-simple form-control ${not empty param.showPassword ? 'form-center-element' : 'form-bottom-element'}" data-container="body">
        <spf:option value="male"><fmt:message key="male"/></spf:option>
        <spf:option value="female"><fmt:message key="female"/></spf:option>
    </spf:select>
    <span class="explanation-select"><fmt:message key="Gender"/></span>
</div>
                    
