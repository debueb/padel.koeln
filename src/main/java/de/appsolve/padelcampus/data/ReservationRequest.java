/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.appsolve.padelcampus.data;

import de.appsolve.padelcampus.constants.CalendarWeekDay;
import de.appsolve.padelcampus.db.model.CustomerEntity;
import de.appsolve.padelcampus.db.model.Offer;
import de.appsolve.padelcampus.db.model.OfferOption;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import static de.appsolve.padelcampus.utils.FormatUtils.TIME_HUMAN_READABLE;

/**
 * @author dominik
 */
public class ReservationRequest extends CustomerEntity {

    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "{NotEmpty.calendarWeekDays}")
    private Set<CalendarWeekDay> calendarWeekDays;

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer startTimeHour;

    private Integer startTimeMinute;

    private Integer endTimeHour;

    private Integer endTimeMinute;

    private String holidayKey;

    private Boolean paymentConfirmed;

    private Boolean publicBooking;

    @NotEmpty(message = "{NotEmpty.offers}")
    private Set<Offer> offers;

    private Set<OfferOption> offerOptions;

    @NotEmpty(message = "{NotEmpty.comment}")
    @Length(min = 3, max = 255, message = "{ReservationRequest.comment}")
    private String comment;

    public Set<CalendarWeekDay> getCalendarWeekDays() {
        if (calendarWeekDays != null && !calendarWeekDays.isEmpty()) {
            return EnumSet.copyOf(calendarWeekDays);
        }
        return Collections.<CalendarWeekDay>emptySet();
    }

    public void setCalendarWeekDays(Set<CalendarWeekDay> calendarWeekDays) {
        this.calendarWeekDays = calendarWeekDays;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Integer getStartTimeHour() {
        return startTimeHour;
    }

    public void setStartTimeHour(Integer startTimeHour) {
        this.startTimeHour = startTimeHour;
    }

    public Integer getStartTimeMinute() {
        return startTimeMinute;
    }

    public void setStartTimeMinute(Integer startTimeMinute) {
        this.startTimeMinute = startTimeMinute;
    }

    public LocalTime getStartTime() {
        return TIME_HUMAN_READABLE.parseLocalTime(getStartTimeHour() + ":" + getStartTimeMinute());
    }

    public Integer getEndTimeHour() {
        return endTimeHour;
    }

    public void setEndTimeHour(Integer endTimeHour) {
        this.endTimeHour = endTimeHour;
    }

    public Integer getEndTimeMinute() {
        return endTimeMinute;
    }

    public void setEndTimeMinute(Integer endTimeMinute) {
        this.endTimeMinute = endTimeMinute;
    }

    public LocalTime getEndTime() {
        return TIME_HUMAN_READABLE.parseLocalTime(getEndTimeHour() + ":" + getEndTimeMinute());
    }

    public String getHolidayKey() {
        return holidayKey;
    }

    public void setHolidayKey(String holidayKey) {
        this.holidayKey = holidayKey;
    }

    public Set<Offer> getOffers() {
        return offers;
    }

    public void setOffers(Set<Offer> offers) {
        this.offers = offers;
    }

    public Set<OfferOption> getOfferOptions() {
        return offerOptions;
    }

    public void setOfferOptions(Set<OfferOption> offerOptions) {
        this.offerOptions = offerOptions;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getPaymentConfirmed() {
        return paymentConfirmed;
    }

    public void setPaymentConfirmed(Boolean paymentConfirmed) {
        this.paymentConfirmed = paymentConfirmed;
    }

    public Boolean getPublicBooking() {
        return publicBooking == null ? Boolean.FALSE : publicBooking;
    }

    public void setPublicBooking(Boolean publicBooking) {
        this.publicBooking = publicBooking;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (CalendarWeekDay weekDay : getCalendarWeekDays()) {
            sb.append(weekDay.name()).append(" ");
        }
        sb.append(startDate).append(" - ").append(endDate);
        return sb.toString();
    }
}
