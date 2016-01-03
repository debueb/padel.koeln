<%@include file="/jsp/include/include.jsp"%>
<jsp:include page="/jsp/include/head.jsp"/>

<div class="row">
    <div class="col-xs-12 col-sm-8 col-sm-offset-2">
        <jsp:include page="/jsp/include/back.jsp"/>

        <div class="page-header"></div>

        <div class="panel panel-info">
            <div class="panel-heading">
                <h4><fmt:message key="RegistrationEmailVerified"/></h4>
            </div>
        </div>


        <div class="alert alert-success"><fmt:message key="EmailSuccessfullyVerified"/></div>
    </div>
</div>

<jsp:include page="/jsp/include/footer.jsp"/>
