<%@include file="/WEB-INF/jsp/include/include.jsp"%>
<jsp:include page="/WEB-INF/jsp/include/head.jsp"/>

<div class="row">
    <div class="col-xs-12 col-sm-10 col-sm-offset-1 col-lg-8 col-lg-offset-2">
        <jsp:include page="/WEB-INF/jsp/include/back.jsp"/>
        <div class="page-header"></div>

        <div class="panel panel-info">
            <div class="panel-heading">
                <h4><fmt:message key="Teams"/></h4>
            </div>
        </div>


        <div class="list-group">
            <a href="/teams/all" class="list-group-item">
                <div class="list-item-text"><fmt:message key="AllTeams"/></div>
            </a>
            <c:forEach var="Event" items="${Events}">
                <a href="/teams/event/${Event.id}" class="list-group-item">
                    <div class="list-item-text"><fmt:message key="TeamsIn"><fmt:param>${Event.name}</fmt:param></fmt:message></div>
                        </a>
            </c:forEach>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>
