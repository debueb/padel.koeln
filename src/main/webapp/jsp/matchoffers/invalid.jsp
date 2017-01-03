<%@include file="/jsp/include/include.jsp"%>
<jsp:include page="/jsp/include/head.jsp"/>

<div class="row">
    <div class="col-xs-12 col-sm-10 col-sm-offset-1 col-lg-8 col-lg-offset-2">
        <jsp:include page="/jsp/include/back.jsp"/>

        <div class="page-header"></div>

        <div class="panel panel-info">
            <div class="panel-heading">
                <h4><fmt:message key="MatchOffer"/></h4>
            </div>
            <div class="panel-body">
                <div class="alert alert-danger"><fmt:message key="InvalidMatchOffer"/></div>

                <a class="btn btn-primary btn-block" href="/matchoffers/add"><fmt:message key="NewMatchOffer"/></a>
                <a class="btn btn-primary btn-block" href="/matchoffers"><fmt:message key="OtherMatchOffers"/></a>
            </div>
        </div>
    </div>
</div>
<jsp:include page="/jsp/include/footer.jsp"/>