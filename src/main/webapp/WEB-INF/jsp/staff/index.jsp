<%@include file="/WEB-INF/jsp/include/include.jsp"%>
<jsp:include page="/WEB-INF/jsp/${path}include/head.jsp"/>
<div class="row">
    <div class="col-xs-12 col-sm-10 col-sm-offset-1 col-lg-8 col-lg-offset-2">
        <jsp:include page="/WEB-INF/jsp/include/module-description.jsp"/>
    
        <div class="container-flex staff-container-flex">
            <c:forEach var="StaffMember" items="${Models}">
                <div class="col-flex staff-col">
                    <div class="staff-container">
                        <div class="staff-flipper">
                            <div class="staff-front staff-bg">
                                <div class="staff-image" style="background-image: url('/images/image/${StaffMember.profileImage.sha256}'); background-repeat: no-repeat; background-size: cover; background-position: center;"></div>
                                <div class="text">
                                    <div class="btn btn-primary btn-walls btn-orange btn-large" style="display: inline;">${StaffMember.name}</div>
                                    <div class="unit-2">${StaffMember.teaser}</div>
                                </div>
                            </div>
                            <div class="staff-back staff-bg">
                                <div class="text">
                                    <div class="btn btn-primary btn-walls btn-orange btn-large" style="display: inline;">${StaffMember.name}</div>
                                    <div class="unit-2">${StaffMember.description}</div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/jsp/${path}include/footer.jsp"/>
