/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.appsolve.padelcampus.data;

import org.joda.time.LocalDate;

import static de.appsolve.padelcampus.constants.Constants.DEFAULT_TIMEZONE;

/**
 * @author dominik
 */
public class DateRange {

    private LocalDate startDate;

    private LocalDate endDate;

    public LocalDate getStartDate() {
        return startDate == null ? new LocalDate(DEFAULT_TIMEZONE) : startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate == null ? new LocalDate(DEFAULT_TIMEZONE) : endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
