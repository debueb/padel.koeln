/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.appsolve.padelcampus.controller;

import de.appsolve.padelcampus.db.model.BaseEntityI;
import org.apache.log4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @param <T>
 * @author dominik
 */
public abstract class BaseEntityController<T extends BaseEntityI> extends BaseController implements BaseEntityControllerI {

    private static final Logger LOG = Logger.getLogger(BaseEntityController.class);

    @RequestMapping(value = "/{id}/delete")
    public ModelAndView getDelete(HttpServletRequest request, @PathVariable("id") Long id) {
        @SuppressWarnings("unchecked")
        T model = (T) getDAO().findById(id);
        if (model == null) {
            return getNotFoundView();
        }
        return getDeleteView(model);
    }

    @RequestMapping(value = "/{id}/delete", method = POST)
    public ModelAndView postDelete(HttpServletRequest request, @PathVariable("id") Long id) {
        try {
            @SuppressWarnings("unchecked")
            T model = (T) getDAO().findById(id);
            if (model == null) {
                return getNotFoundView();
            }
            getDAO().deleteById(id);
        } catch (DataIntegrityViolationException e) {
            @SuppressWarnings("unchecked")
            T model = (T) getDAO().findById(id);
            LOG.warn("Attempt to delete " + model + " failed due to " + e);
            ModelAndView deleteView = getDeleteView(model);
            deleteView.addObject("error", msg.get("CannotDeleteDueToRefrence", new Object[]{model.toString()}));
            return deleteView;
        }
        return redirectToIndex(request);
    }

    protected ModelAndView redirectToIndex(HttpServletRequest request) {
        return new ModelAndView("redirect:/" + getModuleName());
    }


    protected ModelAndView getDeleteView(T model) {
        return new ModelAndView("include/delete", "Model", model);
    }

    public ModelAndView getNotFoundView() {
        return new ModelAndView("include/notfound");
    }
}
