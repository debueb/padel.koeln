/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.appsolve.padelcampus.controller.players;

import de.appsolve.padelcampus.controller.BaseController;
import de.appsolve.padelcampus.data.Mail;
import de.appsolve.padelcampus.db.dao.EventDAOI;
import de.appsolve.padelcampus.db.dao.PlayerDAOI;
import de.appsolve.padelcampus.db.dao.TeamDAOI;
import de.appsolve.padelcampus.db.model.Event;
import de.appsolve.padelcampus.db.model.Participant;
import de.appsolve.padelcampus.db.model.Player;
import de.appsolve.padelcampus.exceptions.MailException;
import de.appsolve.padelcampus.exceptions.ResourceNotFoundException;
import de.appsolve.padelcampus.utils.PlayerUtil;
import de.appsolve.padelcampus.utils.RankingUtil;
import de.appsolve.padelcampus.utils.RequestUtil;
import de.appsolve.padelcampus.utils.SessionUtil;
import de.appsolve.padelcampus.utils.SortUtil;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.SortedMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author dominik
 */
@Controller()
@RequestMapping("/players")
public class PlayersController extends BaseController {
    
    private static final Logger LOG = Logger.getLogger(PlayersController.class);
    
    @Autowired
    PlayerDAOI playerDAO;
    
    @Autowired
    TeamDAOI teamDAO;
    
    @Autowired
    EventDAOI eventDAO;
    
    @Autowired
    SessionUtil sessionUtil;
    
    @Autowired
    RankingUtil rankingUtil;
    
    @RequestMapping(method=GET, value="/player/{UUID}")
    public ModelAndView getPlayer(@PathVariable("UUID") String UUID, HttpServletRequest request){
        return getPlayerView(playerDAO.findByUUID(UUID));
    }
    
    @RequestMapping(method=POST, value="/player/{UUID}")
    public ModelAndView resendAccountVerificationEmail(@PathVariable("UUID") String UUID, HttpServletRequest request) throws MailException, IOException{
        Player player = sessionUtil.getUser(request);
        String accountVerificationLink = PlayerUtil.getAccountVerificationLink(request, player);
        Mail mail = new Mail();
        mail.addRecipient(player);
        mail.setSubject(msg.get("AccountVerificationEmailSubject"));
        mail.setBody(StringEscapeUtils.unescapeJava(msg.get("AccountVerificationEmailBody", new Object[]{player.toString(), accountVerificationLink, RequestUtil.getBaseURL(request)})));
        mailUtils.send(mail, request);
        ModelAndView mav = getPlayerView(playerDAO.findByUUID(UUID));
        mav.addObject("AccountVerificationLinkSent", true);
        return mav;
    }
    
    @RequestMapping(value = "/player/{UUID}/vcard.vcf")
    public void addToContacts(@PathVariable("UUID") String UUID, HttpServletRequest request, HttpServletResponse response) throws IOException{
        // Alternatively: application/octet-stream
        // Depending on the desired browser behaviour
        // Be sure to test thoroughly cross-browser
        Player player = playerDAO.findByUUID(UUID);
        StringBuilder sb = new StringBuilder();
        response.setHeader("Content-type", "text/x-vcard; charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=\""+player.toString()+".vcf\";");
        sb.append("BEGIN:VCARD\n");
        sb.append("VERSION:3.0\n");
        sb.append("N:").append(player.getLastName()).append(";").append(player.getFirstName()).append(";;;\n");
        sb.append("FN:").append(player.toString()).append("\n");
        sb.append("EMAIL;type=INTERNET;type=WORK;type=pref:").append(player.getEmail()).append("\n");
        sb.append("TEL;type=CELL;type=VOICE;type=pref:").append(player.getPhone()).append("\n");
        sb.append("NOTE:Added by ").append(sessionUtil.getCustomer(request)).append("\n");
        sb.append("URL:").append(RequestUtil.getBaseURL(request)).append("/players/player/").append(player.getUUID()).append("\n");
        sb.append("END:VCARD");
        response.getOutputStream().write(sb.toString().getBytes(Charset.forName("UTF-8")));
        response.getOutputStream().flush();
    }
    
    @RequestMapping("/event/{id}")
    public ModelAndView getByEvent(@PathVariable("id") Long eventId){
        Event event = eventDAO.findByIdFetchWithParticipantsAndPlayers(eventId);
        Set<Player> participants = event.getAllPlayers();
        return getPlayersView(event, participants, msg.get("PlayersIn", new Object[]{participants.size(), event.getName()}));
    }

    private ModelAndView getPlayersView(Event event, Collection<Player> players, String title){
        SortedMap<Participant, BigDecimal> ranking = rankingUtil.getPlayerRanking(players);
        ModelAndView mav = new ModelAndView("players/players");
        mav.addObject("RankingMap", SortUtil.sortMap(ranking));
        mav.addObject("title", title);
        mav.addObject("Model", event);
        return mav;
    }

    private ModelAndView getPlayerView(Player player) {
        if (player == null){
            throw new ResourceNotFoundException();
        }
        SortedMap<Participant, BigDecimal> ranking = rankingUtil.getRanking(player.getGender());
        ModelAndView mav = new ModelAndView("players/player", "Player", player);
        mav.addObject("RankingValue", ranking.get(player));
        return mav;
    }
}
