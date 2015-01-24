<%@ page pageEncoding="UTF-8" contentType="text/html" %> 
<%@include file="/jsp/include/include.jsp"%>
<jsp:include page="/jsp/include/head.jsp"/>

<div class="row">
    <div class="col-xs-12 col-sm-8 col-sm-offset-2 col-lg-6 col-lg-offset-3">
        <div class="page-header">
            <h1><fmt:message key="Voucher"/></h1>
        </div>

        <form class="form-signin paymill-form" method="POST" data-payment-type="directdebit">

            <div id="error" class="alert alert-danger">${error}</div>
            
            <div class="relative">
                <input name="voucherUUID" class="form-control" type="text" />
                <div class="explanation"><fmt:message key="Voucher"/></div>
            </div>
            
            <button class="btn btn-primary btn-block btn-form-submit unit" type="submit"><fmt:message key="Book"/></button>
            <a class="btn btn-primary btn-block unit" href="/bookings/booking/${Booking.UUID}/abort?redirect=/bookings/${Booking.bookingDate}/<joda:format value="${Booking.bookingTime}" pattern="HH:mm"/>"><fmt:message key="ChangeBooking"/></a>
            <a class="btn btn-primary btn-block unit ajaxify" href="/bookings/booking/${Booking.UUID}/abort"><fmt:message key="Cancel"/></a>
           
        </form>

    </div>
</div>

<jsp:include page="/jsp/bookings/include/paymill.jsp"/>
<jsp:include page="/jsp/include/footer.jsp"/>