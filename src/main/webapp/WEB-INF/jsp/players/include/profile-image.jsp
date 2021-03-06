<%@include file="/WEB-INF/jsp/include/include.jsp"%>
<span class="polaroid">
    <c:choose>
        <c:when test="${empty Player.profileImage}">
            <span class="fa-stack fa-5x">
                <i class="fa fa-circle fa-stack-2x fa-inverse"></i>
                <i class="fa fa-user-circle fa-stack-1x"></i>
            </span>
            <c:if test="${param.includeName}">
                <span class="block"><a href="/players/player/${Player.UUID}">${Player}</a></span>
            </c:if>
        </c:when>
        <c:otherwise>
            <a href="/players/player/${Player.UUID}">
                <img src="/images/image/${Player.profileImage.sha256}"/>
                <c:if test="${param.includeName}">
                    <span class="block">${Player}</span>
                </c:if>
            </a>
        </c:otherwise>
    </c:choose>
</span>


