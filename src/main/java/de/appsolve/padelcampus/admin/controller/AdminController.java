/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.appsolve.padelcampus.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author dominik
 */
@Controller()
@RequestMapping("/admin")
public class AdminController {
    
    //@Autowired
    //PlayerDAOI playerDAO;
    
    @RequestMapping()
    public ModelAndView getIndex(){
        ModelAndView mav = new ModelAndView("admin/index");
        return mav;
    }
}
