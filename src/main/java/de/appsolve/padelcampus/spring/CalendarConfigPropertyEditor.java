/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.appsolve.padelcampus.spring;

import de.appsolve.padelcampus.db.dao.CalendarConfigDAOI;
import de.appsolve.padelcampus.db.model.CalendarConfig;
import java.beans.PropertyEditorSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author dominik
 */
@Component
public class CalendarConfigPropertyEditor extends PropertyEditorSupport {

    @Autowired
    CalendarConfigDAOI calendarConfigDAO;
    
    @Override
    public void setAsText(String text)
    {
       CalendarConfig config = calendarConfigDAO.findById(Long.parseLong(text));
        setValue(config);
    }
}
