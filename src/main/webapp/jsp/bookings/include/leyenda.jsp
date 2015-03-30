<%@include file="/jsp/include/include.jsp"%>
<div>
    <table class="unit-2 table table-bordered table-leyenda table-fixed">
        <thead>
        <th colspan="${fn:length(Offers)+2}"><fmt:message key="Leyenda"/></th>
        </thead>
        <tbody>
            <c:forEach var="Offer" items="${Offers}">
            <td class="booking-leyenda" style="background-color: ${Offer.hexColor}">${Offer}</td>
        </c:forEach>
        <td class="booking-leyenda booking-booked"><fmt:message key="bookedOut"/></td>
        <td class="booking-leyenda booking-disabled"><fmt:message key="bookingDisabled"/></td>
        </tbody>
    </table>
</div>