<%@include file="/jsp/include/include.jsp"%>
<li>
    <a href="${param.url}" class="ajaxify"><fmt:message key="${param.key}"/>
        <c:if test="${not empty param.image}">
            <span class="list-menu-item-icon">
                <div class="fa fa-lg fa-${param.image}"></div>
            </span> 
        </c:if>
    </a>    
</li>