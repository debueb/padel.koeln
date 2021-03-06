<%@include file="/WEB-INF/jsp/include/include.jsp"%>
<c:choose>
    <c:when test="${empty Models}">
        <div class="alert alert-info"><fmt:message key="NoTournaments"/></div>
    </c:when>
    <c:otherwise>
        <table class="table table-striped">
            <thead>
            <th><fmt:message key="StartDate"/></th>
            <th><fmt:message key="EndDate"/></th>
            <th><fmt:message key="Category"/></th>
            <th><fmt:message key="Name"/></th>
            <th><fmt:message key="Mode"/></th>
            <th><fmt:message key="Operator"/></th>
        </thead>
        <tbody>
            <c:forEach var="Event" items="${Models}">
                
                <c:set var="url" value="http://${Event.customer.domainName}/events/event/${Event.id}"/>
                <tr>
                    <td>
                        <a href="${url}">
                            <joda:format value="${Event.startDate}" pattern="EEEE, dd. MMMM yyyy"/>
                        </a>
                    </td>
                    <td>
                        <a href="${url}">
                            <joda:format value="${Event.endDate}" pattern="EEEE, dd. MMMM yyyy"/>
                        </a>
                    </td>
                    <td><a href="${url}"><fmt:message key="Mode_${Event.gender}"/></a></td>
                    <td><a href="${url}">${Event.name}</a></td>
                    <td><a href="${url}"><fmt:message key="${Event.eventType}"/></a></td>
                    <td><a href="${url}">${Event.customer}</a></td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</c:otherwise>
</c:choose>
