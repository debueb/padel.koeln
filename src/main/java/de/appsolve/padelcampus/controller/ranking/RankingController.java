/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.appsolve.padelcampus.controller.ranking;

import de.appsolve.padelcampus.constants.Gender;
import de.appsolve.padelcampus.constants.ModuleType;
import de.appsolve.padelcampus.controller.BaseController;
import de.appsolve.padelcampus.db.dao.TeamDAOI;
import de.appsolve.padelcampus.db.model.Module;
import de.appsolve.padelcampus.db.model.Participant;
import de.appsolve.padelcampus.utils.ModuleUtil;
import de.appsolve.padelcampus.utils.RankingUtil;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author dominik
 */
@Controller()
@RequestMapping("/ranking")
public class RankingController extends BaseController {
    
    @Autowired
    RankingUtil rankingUtil;
    
    @Autowired
    TeamDAOI teamDAO;
    
    @Autowired
    ModuleUtil moduleUtil;
    
    @RequestMapping
    public ModelAndView getIndex(HttpServletRequest request){
        Module module = moduleUtil.getCustomerModule(request, ModuleType.Ranking);
        return getIndexView(module.getTitle(), module.getDescription());
    }
    
    @RequestMapping("{gender}/{category}")
    public ModelAndView getRanking(@PathVariable("gender") Gender gender, @PathVariable("category") String category){
        ModelAndView mav = new ModelAndView("ranking/ranking");
        mav.addObject("gender", gender);
        mav.addObject("category", category);
        SortedMap<Participant, BigDecimal> rankings = null;
        switch (category){
            case "individual":
                rankings = rankingUtil.getRanking(gender);
                break;
            case "team":
                rankings = rankingUtil.getTeamRanking(gender);
                break;
        }
        BigDecimal hundred = new BigDecimal("100");
        if (rankings != null){
            Iterator<? extends Participant> iterator = rankings.keySet().iterator();
            while (iterator.hasNext()){
                Participant p = iterator.next();
                BigDecimal ranking = rankings.get(p);
                ranking = ranking.divide(hundred);
                rankings.put(p, ranking);
            }
        }
        
        mav.addObject("Rankings", rankings);
        mav.addObject("path", getPath());
        return mav;
    }
    
    protected ModelAndView getIndexView(String title, String description){
        ModelAndView mav = new ModelAndView("ranking/index");
        mav.addObject("path", getPath());
        mav.addObject("title", title);
        mav.addObject("description", description);
        return mav;
    }
}
