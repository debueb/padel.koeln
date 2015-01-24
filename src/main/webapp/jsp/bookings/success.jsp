<%@include file="/jsp/include/include.jsp"%>
<jsp:include page="/jsp/include/head.jsp"/>

<div class="row">
    <div class="col-xs-12 col-sm-8 col-sm-offset-2 col-lg-6 col-lg-offset-3">
        <div class="page-header">
            <h1><fmt:message key="BookCourt"/></h1>
        </div>
            <c:choose>
                <c:when test="${not empty error}">
                    <div class="alert alert-danger" role="alert">${error}</div>
                </c:when>
                <c:otherwise>
                    <div class="alert alert-success" role="alert"><fmt:message key="BookingSuccessMessage"><fmt:param value="${sessionScope.booking.player.email}"/></fmt:message></div>
                </c:otherwise>
            </c:choose>
        
    </div>
</div>

<jsp:include page="/jsp/include/footer.jsp"/>
