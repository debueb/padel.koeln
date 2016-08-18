<%@include file="/jsp/include/include.jsp"%>
<jsp:include page="/jsp/include/head.jsp"/>

<div class="row">
    <div class="col-xs-12 col-sm-10 col-sm-offset-1 col-lg-8 col-lg-offset-2">
        <div class="page-header"></div>
        <jsp:include page="/jsp/events/include/info.jsp"/>
        <div class="panel panel-info">
            <div class="panel-heading">
                <h4>${Title}</h4>
            </div>
            <div class="panel-body">
                <div class="list-group">
                    <c:forEach var="Participant" items="${Teams}">
                        <a href="/teams/team/${Participant.UUID}" class="list-group-item ajaxify">
                            <div class="list-item-text">${Participant}</div>
                        </a>
                    </c:forEach>
                </div>
            </div>
        </div>

    </div>
</div>

<jsp:include page="/jsp/include/footer.jsp"/>
