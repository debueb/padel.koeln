<%@include file="/jsp/include/include.jsp"%>
<jsp:include page="/jsp/include/head.jsp"/>

<div class="row">
    <div class="col-xs-12 col-lg-10 col-lg-offset-1">
        <jsp:include page="/jsp/include/back.jsp"/>

        <div class="page-header"></div>

        <div class="panel panel-info">
            <div class="panel-heading">
                <h4><fmt:message key="BookingsAndReservations"/></h4>
            </div>
            <div class="panel-body">
                <jsp:include page="/jsp/admin/include/daterange.jsp"/>

                <div class="table-responsive" style="margin-top: 40px;">
                    <table class="table table-striped table-bordered table-centered datatable">
                        <thead>
                        <th><fmt:message key="GameDate"/></th>
                        <th><fmt:message key="Day"/></th>
                        <th><fmt:message key="Time"/></th>
                        <th><fmt:message key="BookingDate"/></th>
                        <th><fmt:message key="Offer"/></th>
                        <th><fmt:message key="ReservedBy"/></th>
                        <th><fmt:message key="Comment"/></th>
                        <th><fmt:message key="PaymentMethod"/></th>
                        <th><fmt:message key="Paid"/></th>
                        <th><fmt:message key="Invoice"/></th>
                        <th><fmt:message key="Price"/></th>
                        </thead>
                        <tbody>
                            <c:forEach items="${Bookings}" var="Booking">
                                <c:set var="url" value="/admin/bookings/reservations/booking/${Booking.id}"/>
                                <tr>
                                    <td><a class="ajaxify" href="${url}"><joda:format value="${Booking.bookingDate}" pattern="yyyy-MM-dd"/></a></td>
                                    <td><a class="ajaxify" href="${url}"><joda:format value="${Booking.bookingDate}" pattern="EE"/></a></td>
                                    <td><a class="ajaxify" href="${url}"><joda:format value="${Booking.bookingTime}" pattern="HH:mm"/> - <joda:format value="${Booking.bookingEndTime}" pattern="HH:mm"/></a></td>
                                    <td><a class="ajaxify" href="${url}"><joda:format value="${Booking.blockingTime}" pattern="yyyy-MM-dd"/></a></td>
                                    <td><a class="ajaxify" href="${url}">${Booking.name}</a></td>
                                    <td><a class="ajaxify" href="/admin/players/edit/${Booking.player.id}">${Booking.player}</a></td>
                                    <td><a class="ajaxify" href="${url}">${Booking.comment}</a></td>
                                    <td><a class="ajaxify" href="${url}"><fmt:message key="${Booking.paymentMethod}"/></a></td>
                                    <td data-sort="${Booking.paymentConfirmed}"><a class="ajaxify" href="${url}"><i class="fa fa-${Booking.paymentConfirmed ? 'check' : 'close'}"></i></a></td>
                                    <td><a class="ajaxify" href="/invoices/booking/${Booking.UUID}"><i class="fa fa-file-text"></i></a></td>
                                    <td><a class="ajaxify" href="${url}">${Booking.amount} ${Booking.currency.symbol}</a></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
                <div class="unit-2">
                    <fmt:message key="TotalAmount">
                        <fmt:param value="${Total}"/>
                    </fmt:message>
                </div>
                <div class="unit-2">
                    <a class="btn btn-primary btn-block ajaxify" href="/admin/bookings/reservations/add"><fmt:message key="AddReservation"/></a>
                </div>
                <div class="unit">
                    <a class="btn btn-primary btn-block ajaxify" href="/admin/bookings/reservations/print/${DateRange.startDate}/${DateRange.endDate}"><fmt:message key="PrintAll"/></a>
                </div>
            </div>
        </div>
    </div>
</div>
<jsp:include page="/jsp/include/datatables.jsp"/>
<jsp:include page="/jsp/include/footer.jsp"/>

