<%@include file="/jsp/include/include.jsp"%>
<jsp:include page="/jsp/include/head.jsp"/>

<div class="row">
    <div class="col-xs-12 col-sm-8 col-sm-offset-2 col-lg-6 col-lg-offset-3">
        <jsp:include page="/jsp/include/back.jsp"/>
        <div class="page-header">
            <h1><fmt:message key="Teams"/></h1>
        </div>

        <div class="list-group">
            <a href="/teams/all" class="list-group-item ajaxify">
                <div class="list-item-text"><fmt:message key="AllTeams"/></div>
            </a>
            <c:forEach var="Event" items="${Events}">
                <a href="/teams/event/${Event.id}" class="list-group-item ajaxify">
                    <div class="list-item-text"><fmt:message key="TeamsIn"><fmt:param>${Event.name}</fmt:param></fmt:message></div>
                </a>
            </c:forEach>
        </div>
    </div>
</div>

<jsp:include page="/jsp/include/footer.jsp"/>
