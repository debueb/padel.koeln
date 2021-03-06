/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.appsolve.padelcampus.db.dao;

import de.appsolve.padelcampus.db.dao.generic.BaseEntityDAOI;
import de.appsolve.padelcampus.db.model.Booking;
import de.appsolve.padelcampus.db.model.Player;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.util.List;

/**
 * @author dominik
 */
public interface BookingDAOI extends BaseEntityDAOI<Booking> {

    public Booking findByUUID(String UUID);

    public Booking findByUUIDWithEvent(String UUID);

    public Booking findByUUIDWithEventAndPlayers(String UUID);

    public Booking findByIdWithOfferOptions(Long id);

    public List<Booking> findBlockedBookingsForDate(LocalDate date);

    public List<Booking> findBlockedBookingsBetween(LocalDate startDate, LocalDate endDate);

    public List<Booking> findBlockedBookings();

    public List<Booking> findActiveBookingsBetween(LocalDate startDate, LocalDate endDate);

    public List<Booking> findActiveReservationsBetween(LocalDate startDate, LocalDate endDate);

    public List<Booking> findActiveBookingsByPlayerBetween(Player player, LocalDate startDate, LocalDate endDate);

    public List<Booking> findOfferBookingsByPlayer(Player player);

    public List<Booking> findAllBookingsByPlayer(Player player);

    public List<Booking> findByBlockingTimeAndComment(LocalDateTime blockingTime, String comment);
}
