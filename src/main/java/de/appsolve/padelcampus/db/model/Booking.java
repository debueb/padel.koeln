/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.appsolve.padelcampus.db.model;

import de.appsolve.padelcampus.constants.BookingType;
import de.appsolve.padelcampus.constants.Currency;
import de.appsolve.padelcampus.constants.PaymentMethod;
import de.appsolve.padelcampus.utils.FormatUtils;
import static de.appsolve.padelcampus.utils.FormatUtils.TWO_FRACTIONAL_DIGITS_FORMAT;
import java.math.BigDecimal;
import java.math.MathContext;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

/**
 *
 * @author dominik
 */
@Entity
public class Booking extends BaseEntity{
    
    @Transient
    private static final long serialVersionUID = 1L;
    
    @OneToOne
    private Player player;
    
    @Column
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate bookingDate;
    
    @Column
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalTime")
    private LocalTime bookingTime;
    
    @Column
    private Long duration;
    
    @Column
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    
    @Column
    private BigDecimal amount;
    
    @Column
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Column
    @Enumerated(EnumType.STRING)
    private BookingType bookingType;
    
    @OneToOne
    private Voucher voucher;
    
    @Column
    private String comment;
    
    /**
     * indicates the time that the user has entered the checkout (payment) phase
     * to indicate that the court should be blocked to avoid duplicate bookings
     * this booking is to be removed if the payment is not confirmed after the session timeout
     * or if the user cancels the payment
     */
    @Column
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    private LocalDateTime blockingTime;
    
    /**
     * if the booking has been confirmed to the user within the UI
     */
    @Column
    private Boolean confirmed;
    
    /**
     * if the booking has been cancelled
     */
    @Column
    private Boolean cancelled;
    
    /**
     * reason for cancellation
     */
    private String cancelReason;
    
    /**
     * if the booking has been confirmed to the user by mail
     */
    @Column
    private Boolean confirmationMailSent;
    
    /**
     * if the payment has been confirmed by the payment provider
     */
    @Column
    private Boolean paymentConfirmed;            
    
    @Column
    private String UUID;
    
    @Column
    private Integer courtNumber;
    
    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public LocalTime getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(LocalTime bookingTime) {
        this.bookingTime = bookingTime;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }
    
    public LocalTime getBookingEndTime(){
        if (bookingTime!=null && duration!=null){
            return bookingTime.plusMinutes(getDuration().intValue());
        }
        return null;
    }
    
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public BigDecimal getAmount() {
        return amount;
    }
    
    public String getAmountDouble(){
        return TWO_FRACTIONAL_DIGITS_FORMAT.format(getAmount().doubleValue());
    }
    
    public String getAmountInt(){
        BigDecimal value = amount.multiply(new BigDecimal("100"), MathContext.DECIMAL64);
        return value.toPlainString();
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BookingType getBookingType() {
        return bookingType;
    }

    public void setBookingType(BookingType bookingType) {
        this.bookingType = bookingType;
    }

    public LocalDateTime getBlockingTime() {
        return blockingTime;
    }

    public void setBlockingTime(LocalDateTime blockingTime) {
        this.blockingTime = blockingTime;
    }

    public Boolean getConfirmed() {
        return confirmed == null ? false : confirmed;
    }

    public void setConfirmed(Boolean confirmed) {
        this.confirmed = confirmed;
    }

    public Boolean getCancelled() {
        return cancelled == null ? false : cancelled;
    }

    public void setCancelled(Boolean cancelled) {
        this.cancelled = cancelled;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public Boolean getConfirmationMailSent() {
        return confirmationMailSent;
    }

    public void setConfirmationMailSent(Boolean confirmationMailSent) {
        this.confirmationMailSent = confirmationMailSent;
    }

    public Boolean getPaymentConfirmed() {
        return paymentConfirmed == null ? false : paymentConfirmed;
    }

    public void setPaymentConfirmed(Boolean paymentConfirmed) {
        this.paymentConfirmed = paymentConfirmed;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public Integer getCourtNumber() {
        return courtNumber;
    }

    public void setCourtNumber(Integer courtNumber) {
        this.courtNumber = courtNumber;
    }

    public Voucher getVoucher() {
        return voucher;
    }

    public void setVoucher(Voucher voucher) {
        this.voucher = voucher;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
    
    @Override
    public String getDisplayName() {
        return getPlayer().getDisplayName()+" "+getBookingDate().toString(FormatUtils.DATE_WITH_DAY) + " " + getBookingTime().toString(FormatUtils.TIME_HUMAN_READABLE);
    }
    
    @Override
    public String toString(){
        return getDisplayName();
    }
}
