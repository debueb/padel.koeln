<%@include file="/jsp/include/include.jsp"%>
<jsp:include page="/jsp/include/head.jsp"/>

<div class="row">
    <div class="col-xs-12 col-sm-6 col-sm-offset-3">
        <jsp:include page="/jsp/include/back.jsp"/>
        
        <div class="page-header">
            <h1><fmt:message key="ForgotPassword"/></h1>
        </div>

        <spf:form class="form-signin" role="form" modelAttribute="Model">
            <div class="alert alert-danger"><spf:errors path="*"/></div>
            <fmt:message key="EmailAddress" var="placeholder"/>
            <spf:input path="email" type="email" class="form-control" placeholder="${placeholder}"/>
            <button class="btn btn-primary btn-block unit" type="submit"><fmt:message key="ForgotPassword"/></a>
      </spf:form>
    </div>
</div>

<jsp:include page="/jsp/include/footer.jsp"/>
