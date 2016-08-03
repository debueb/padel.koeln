<%@include file="/jsp/include/include.jsp"%>
<jsp:include page="/jsp/include/head.jsp"/>

<div class="row">
    <div class="col-xs-12 col-sm-10 col-sm-offset-1 col-lg-8 col-lg-offset-2">
        <jsp:include page="/jsp/include/back.jsp"/>

        <div class="page-header"></div>

        <div class="panel panel-info">
            <div class="panel-heading">
                <h4><fmt:message key="Design"/></h4>
            </div>
            <div class="panel-body">

                <form method="POST" class="form-signin" enctype="multipart/form-data">
                    <c:forEach var="Attribute" items="${Colors}">
                        <c:choose>
                            <c:when test="${fn:endsWith(Attribute.name, 'Color')}">
                                <div class="input-group color-picker unit">
                                    <span class="input-group-addon"><i></i></span>
                                    <span class="relative">
                                        <input type="text" name="${Attribute.id}" class="form-control" value="${empty Attribute.cssValue ? Attribute.cssDefaultValue : Attribute.cssValue}" />
                                        <span class="explanation"><fmt:message key="${Attribute.name}"/></span>
                                    </span>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <hr/>
                                <figure class="picture unit">
                                    <div class="text-center"><fmt:message key="BackgroundImage"/>
                                    <div class="text-center"><fmt:message key="BackgroundImageDesc"/>
                                    <div class="picture-subtext text-center"><fmt:message key="ClickImageToChange"/></div>
                                    <div class="unit" style="width: 100%; height: 100%; background-size: 100% 100%; content: ${empty Attribute.cssValue ? 'url(\'/images/bg.jpg\')' : Attribute.cssValue}"></div>
                                </figure>
                                <input type="file" capture="camera" accept="image/*" name="backgroundImage" class="picture-input hidden"/>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                    
                    <hr/>
                    <figure class="picture unit">
                        <div class="text-center"><fmt:message key="TouchIcon"/>
                        <div class="text-center"><fmt:message key="TouchIconDesc"/></div>
                        <div class="picture-subtext text-center"><fmt:message key="ClickImageToChange"/></div>
                        <div class="unit" style="margin: 0 auto; width: 192px; height: 192px; background-size: 192px 192px; background-image: url('${sessionScope.customer.touchIconPath}');"></div>
                    </figure>
                    <input type="file" capture="camera" accept="image/*" name="touchIcon" class="picture-input hidden"/>
                    
                    <hr/>
                    <figure class="picture unit">
                        <div class="text-center"><fmt:message key="CompanyLogo"/></div>
                        <div class="text-center"><fmt:message key="CompanyLogoDesc"/></div>
                        <div class="picture-subtext text-center"><fmt:message key="ClickImageToChange"/></div>
                        <div class="unit" style="margin: 0 auto; width: auto; height: 100px; background-size: auto 100px; content: url('${sessionScope.customer.companyLogoPath}');"></div>
                    </figure>
                    <input type="file" capture="camera" accept="image/*" name="companyLogo" class="picture-input hidden"/>
                    
                    
                    <button class="btn btn-primary btn-block btn-form-submit unit" type="submit"><fmt:message key="Save"/></button>
                </form>
            </div>
        </div>
    </div>
</div>
<jsp:include page="/jsp/admin/include/colorpicker.jsp"/>
