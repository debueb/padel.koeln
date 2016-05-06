<%@include file="/jsp/include/include.jsp"%>
<jsp:include page="/jsp/include/head.jsp"/>

<div class="row">
    <div class="col-xs-12 col-sm-10 col-sm-offset-1 col-lg-8 col-lg-offset-2">
        <jsp:include page="/jsp/include/back.jsp"/>

        <div class="page-header"></div>

        <div class="panel panel-info">
            <div class="panel-heading">
                <h4><fmt:message key="${gender}_${category}_ranking"/></h4>
            </div>
            <div class="panel-body">

                <c:choose>
                    <c:when test="${empty Rankings}">
                        <div class="alert alert-info"><fmt:message key="NoRankingsYet"/></div>
                    </c:when>
                    <c:otherwise>
                        <div style="margin-bottom: 20px;">
                            <table class="table table-bordered table-scores">
                                <thead>
                                <th class="text-center">#</th>
                                <th><fmt:message key="Name"/></th>
                                <th class="text-center"><fmt:message key="Points"/></th>
                                </thead>
                                <tbody>
                                    <c:forEach var="Ranking" items="${Rankings}" varStatus="status">
                                        <c:set var="Participant" value="${Ranking.key}"/>
                                        <c:choose>
                                            <c:when test="${Participant.discriminatorValue == 'Player'}">
                                                <c:set var="urlParticipant" value="/players/player/${Participant.id}"/>
                                            </c:when>
                                            <c:otherwise>
                                                <c:set var="urlParticipant" value="/teams/team/${Participant.id}"/>
                                            </c:otherwise>
                                        </c:choose>
                                        <tr>
                                            <td class="text-center">${status.index+1}</td>
                                            <td><a href="${urlParticipant}" class="ajaxify">${Participant}</a></td>
                                            <td class="text-center">${Ranking.value}</td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>


        <jsp:include page="/jsp/ranking/include/links.jsp"/>

    </div>
</div>

<jsp:include page="/jsp/include/footer.jsp"/>