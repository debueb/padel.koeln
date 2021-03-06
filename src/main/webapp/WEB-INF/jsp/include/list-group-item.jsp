<%@include file="/WEB-INF/jsp/include/include.jsp"%>
<a href="${param.href}" class="list-group-item" id="${param.key}" data-anchor="${param.anchor}">
    <div class="list-item-text">
        <c:choose>
            <c:when test="${empty param.title}">
                <fmt:message key="${param.key}"/>
            </c:when>
            <c:otherwise>
                ${param.title}
            </c:otherwise>
        </c:choose>
    </div>
    <c:if test="${not empty param.icon}">
        <div class="list-group-item-icon"><div class="fa fa-${param.icon} fa-fw"></div></div>
    </c:if>
</a>