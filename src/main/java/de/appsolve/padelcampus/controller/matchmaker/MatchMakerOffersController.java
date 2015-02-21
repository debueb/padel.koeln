/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.appsolve.padelcampus.controller.matchmaker;

import com.microtripit.mandrillapp.lutung.model.MandrillApiError;
import static de.appsolve.padelcampus.constants.Constants.BOOKING_DEFAULT_VALID_FROM_HOUR;
import de.appsolve.padelcampus.constants.SkillLevel;
import de.appsolve.padelcampus.controller.BaseEntityController;
import de.appsolve.padelcampus.data.EmailContact;
import de.appsolve.padelcampus.data.Mail;
import de.appsolve.padelcampus.db.dao.GenericDAOI;
import de.appsolve.padelcampus.db.dao.MatchOfferDAOI;
import de.appsolve.padelcampus.db.dao.NotificationSettingDAOI;
import de.appsolve.padelcampus.db.dao.PlayerDAOI;
import de.appsolve.padelcampus.db.model.MatchOffer;
import de.appsolve.padelcampus.db.model.NotificationSetting;
import de.appsolve.padelcampus.db.model.Player;
import de.appsolve.padelcampus.spring.LocalDateEditor;
import de.appsolve.padelcampus.utils.FormatUtils;
import static de.appsolve.padelcampus.utils.FormatUtils.DATE_HUMAN_READABLE_PATTERN;
import de.appsolve.padelcampus.utils.MailUtils;
import de.appsolve.padelcampus.utils.RequestUtil;
import de.appsolve.padelcampus.utils.SessionUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.jadira.usertype.spi.utils.lang.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author dominik
 */
@Controller()
@RequestMapping("/matchmaker/offers")
public class MatchMakerOffersController extends BaseEntityController<MatchOffer> {

    private static final Logger log = Logger.getLogger(MatchMakerOffersController.class);

    @Autowired
    PlayerDAOI playerDAO;

    @Autowired
    SessionUtil sessionUtil;

    @Autowired
    MatchOfferDAOI matchOfferDAO;
    
    @Autowired
    NotificationSettingDAOI notificationSettingDAO;

    @Autowired
    Validator validator;
    
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(LocalDate.class, new LocalDateEditor(DATE_HUMAN_READABLE_PATTERN, false));

        binder.registerCustomEditor(Set.class, "players", new CustomCollectionEditor(Set.class) {
            @Override
            protected Object convertElement(Object element) {
                Long id = Long.parseLong((String) element);
                return playerDAO.findById(id);
            }
        });
    }

    @RequestMapping()
    public ModelAndView getIndex(HttpServletRequest request) {
        Player user = sessionUtil.getUser(request);
        List<MatchOffer> offers = matchOfferDAO.findBy(user);
        ModelAndView mav = new ModelAndView("matchmaker/offers/index", "Models", offers);
        return mav;
    }

    @RequestMapping(value = "edit")
    public ModelAndView getEdit(HttpServletRequest request) {
        Player user = sessionUtil.getUser(request);
        if (user == null) {
            return new ModelAndView("include/loginrequired", "title", msg.get("EditOffer"));
        }
        MatchOffer matchOffer = new MatchOffer();
        matchOffer.setStartDate(new LocalDate().plusDays(1));
        matchOffer.setStartTimeHour(BOOKING_DEFAULT_VALID_FROM_HOUR);
        matchOffer.setMinPlayersCount(4);
        matchOffer.setMaxPlayersCount(4);
        Set<Player> players = new HashSet<>();
        players.add(user);
        matchOffer.setPlayers(players);
        return getEditView(matchOffer);
    }

    @RequestMapping(value = "edit/{id}")
    public ModelAndView getEdit(HttpServletRequest request, @PathVariable("id") Long id) {
        MatchOffer offer = matchOfferDAO.findById(id);
        return getEditView(offer);
    }

    @RequestMapping(value = {"edit", "edit/{id}"}, method = POST)
    public ModelAndView postEdit(HttpServletRequest request, @ModelAttribute("Model") MatchOffer model, BindingResult result) {
        Player user = sessionUtil.getUser(request);
        if (user == null) {
            return new ModelAndView("include/loginrequired", "title", msg.get("EditOffer"));
        }
        
        //ToDo: make sure court is bookable
        ModelAndView editView = getEditView(model);
        
        validator.validate(model, result);
        if (result.hasErrors()) {
            return editView;
        }
        
        try {
            model.setOwner(user);
            matchOfferDAO.saveOrUpdate(model);
        
            //inform other users about new offers
            if (model.getId()==null){
                List<NotificationSetting> notificationSettings = notificationSettingDAO.findFor(model);
                if (!notificationSettings.isEmpty()){
                    Mail mail = new Mail(request);
                    mail.setSubject(msg.get("NewMatchOfferMailSubject"));
                    mail.setBody(getNewMatchOfferMailBody(request, model));
                    
                    for (NotificationSetting setting: notificationSettings){
                        Player player = setting.getPlayer();
                        if (!player.equals(user)){
                            mail.addRecipient(player);
                        }
                    }
                    if (!mail.getRecipients().isEmpty()){
                        MailUtils.send(mail);
                    }
                }
            }
        } catch (MandrillApiError | IOException e){
            log.error("Error while sending mails about new match offer: "+ e.getMessage());
        }
        return new ModelAndView("redirect:/matchmaker/offers/offer/"+model.getId());
    }

    @RequestMapping(value = "offer/{id}")
    public ModelAndView getShow(HttpServletRequest request, @PathVariable("id") Long id) {
        MatchOffer offer = matchOfferDAO.findById(id);
        return getShowView(request, offer);
    }

    @RequestMapping(value = "offer/{id}", method = POST)
    public ModelAndView postOffer(HttpServletRequest request, @PathVariable("id") Long id) {
        Player user = sessionUtil.getUser(request);
        if (user == null) {
            return new ModelAndView("include/loginrequired", "title", msg.get("EditOffer"));
        }

        MatchOffer offer = matchOfferDAO.findById(id);
        ModelAndView view = getShowView(request, offer);
        String acceptParticipation = request.getParameter("accept-participation");
        String cancelParticipation = request.getParameter("cancel-participation");

        if (StringUtils.isEmpty(acceptParticipation) && StringUtils.isEmpty(cancelParticipation)) {
            view.addObject("error", msg.get("PleaseConfirmYourChoice"));
            return view;
        }

        String baseURL = RequestUtil.getBaseURL(request);
        String offerURL = getOfferURL(request, offer);

        Mail existingParticipantsEmail = new Mail();
        existingParticipantsEmail.setFrom(user.getEmail());
        existingParticipantsEmail.setRecipients(new ArrayList<EmailContact>(offer.getPlayers()));

        try {

            if (!StringUtils.isEmpty(acceptParticipation) && acceptParticipation.equals("on")) {
                //inform other players about new participant
                existingParticipantsEmail.setSubject(msg.get("MatchOfferNewParticipantEmailSubject"));

                Integer remainingRequiredPlayersCount = offer.getMinPlayersCount() - offer.getPlayers().size() - 1;
                String body;
                if (remainingRequiredPlayersCount > 0) {
                    body = msg.get("MatchOfferNewParticipantEmailBody", new Object[]{user.toString(), offer.toString(), remainingRequiredPlayersCount, offerURL, baseURL});
                } else {
                    String bookingURL = baseURL + "/bookings";
                    body = msg.get("MatchOfferNewMatchEmailBody", new Object[]{user.toString(), offer.toString(), offerURL, bookingURL, baseURL});
                }
                existingParticipantsEmail.setBody(body);

                Mail newParticipantEmail = new Mail(request);
                newParticipantEmail.addRecipient(user);
                newParticipantEmail.setSubject(msg.get("MatchOfferParticipationConfirmationEmailSubject"));
                newParticipantEmail.setBody(msg.get("MatchOfferParticipationConfirmationEmailBody", new Object[]{user.toString(), offer.toString(), offerURL, baseURL}));

                MailUtils.send(existingParticipantsEmail);
                MailUtils.send(newParticipantEmail);

                //add player to offer
                offer.getPlayers().add(user);

                //confirm participation to user
                view.addObject("msg", msg.get("MatchMakerAcceptConfirmation"));
                
            }

            if (!StringUtils.isEmpty(cancelParticipation) && cancelParticipation.equals("on")) {
                //inform all players about cancellation
                existingParticipantsEmail.setSubject(msg.get("MatchOfferParticipantCancelledEmailSubject"));
                Integer remainingRequiredPlayersCount = offer.getMinPlayersCount() - offer.getPlayers().size() + 1;
                existingParticipantsEmail.setBody(msg.get("MatchOfferParticipantCancelledEmailBody", new Object[]{user.toString(), offer.toString(), remainingRequiredPlayersCount, offerURL, baseURL}));
                
                MailUtils.send(existingParticipantsEmail);
                
                //remove player from offer
                offer.getPlayers().remove(user);
                
                //confirm cancellation to user
                view.addObject("msg", msg.get("MatchMakerCancelConfirmation"));
            }
            
            //persist changes
            matchOfferDAO.saveOrUpdate(offer);
        } catch (MandrillApiError | IOException e) {
            log.error(e);
            view.addObject("error", msg.get("FailedToSendEmail"));
            return view;
        }
        return view;
    }

    private ModelAndView getEditView(MatchOffer model) {
        ModelAndView mav = new ModelAndView("matchmaker/offers/edit", "Model", model);
        mav.addObject("SkillLevels", SkillLevel.values());
        mav.addObject("Players", playerDAO.findAll());
        return mav;
    }

    private ModelAndView getShowView(HttpServletRequest request, MatchOffer model) {
        ModelAndView mav = new ModelAndView("matchmaker/offers/offer", "Model", model);
        mav.addObject("OfferURL", getOfferURL(request, model));
        mav.addObject("NewMatchOfferMailSubject", msg.get("NewMatchOfferMailSubject"));
        mav.addObject("NewMatchOfferMailBody", getNewMatchOfferMailBody(request, model).replace("\n", "%0A"));
        return mav;
    }

    @Override
    public GenericDAOI getDAO() {
        return matchOfferDAO;
    }

    @Override
    public String getModuleName() {
        return "matchmaker";
    }

    private String getNewMatchOfferMailBody(HttpServletRequest request, MatchOffer model) {
        Object[] params = new Object[7];
        String baseURL = RequestUtil.getBaseURL(request);
        params[0] = model.getOwner();
        params[1] = model.getStartDate().toString(FormatUtils.DATE_HUMAN_READABLE);
        params[2] = model.getStartTime().toString(FormatUtils.TIME_HUMAN_READABLE);
        params[3] = model.getPlayers();
        params[4] = baseURL+"/matchmaker/offers/offer/"+model.getId();
        params[5] = baseURL+"/matchmaker/notificationsettings";
        params[6] = baseURL;
        return msg.get("NewMatchOfferMailBody", params);
    }

    private String getOfferURL(HttpServletRequest request, MatchOffer offer) {
        return RequestUtil.getBaseURL(request) + "/matchmaker/offers/offer/" + offer.getId();
    }
}
