/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.appsolve.padelcampus.controller.matchoffers;

import com.google.common.collect.Sets;
import de.appsolve.padelcampus.constants.Constants;
import de.appsolve.padelcampus.constants.ModuleType;
import de.appsolve.padelcampus.constants.SkillLevel;
import de.appsolve.padelcampus.controller.BaseEntityController;
import de.appsolve.padelcampus.data.Mail;
import de.appsolve.padelcampus.db.dao.MatchOfferDAOI;
import de.appsolve.padelcampus.db.dao.PlayerDAOI;
import de.appsolve.padelcampus.db.dao.generic.BaseEntityDAOI;
import de.appsolve.padelcampus.db.model.MatchOffer;
import de.appsolve.padelcampus.db.model.Player;
import de.appsolve.padelcampus.exceptions.MailException;
import de.appsolve.padelcampus.spring.LocalDateEditor;
import de.appsolve.padelcampus.utils.FormatUtils;
import de.appsolve.padelcampus.utils.ModuleUtil;
import de.appsolve.padelcampus.utils.RequestUtil;
import de.appsolve.padelcampus.utils.SessionUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static de.appsolve.padelcampus.constants.Constants.BOOKING_DEFAULT_VALID_FROM_HOUR;
import static de.appsolve.padelcampus.utils.FormatUtils.DATE_HUMAN_READABLE_PATTERN;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author dominik
 */
@Controller()
@RequestMapping("/matchoffers")
public class MatchOffersController extends BaseEntityController<MatchOffer> {

    private static final Logger LOG = Logger.getLogger(MatchOffersController.class);

    @Autowired
    PlayerDAOI playerDAO;

    @Autowired
    SessionUtil sessionUtil;

    @Autowired
    MatchOfferDAOI matchOfferDAO;

    @Autowired
    Validator validator;

    @Autowired
    ModuleUtil moduleUtil;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(LocalDate.class, new LocalDateEditor(DATE_HUMAN_READABLE_PATTERN, false));

        binder.registerCustomEditor(Set.class, "players", new CustomCollectionEditor(Set.class) {
            @Override
            protected Object convertElement(Object element) {
                return playerDAO.findByUUID((String) element);
            }
        });
    }

    @RequestMapping()
    public ModelAndView getIndex(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("matchoffers/index");
        mav.addObject("Models", matchOfferDAO.findCurrent());
        mav.addObject("Module", moduleUtil.getCustomerModule(request, ModuleType.MatchOffers));
        Player user = sessionUtil.getUser(request);
        if (user != null) {
            mav.addObject("PersonalOffers", matchOfferDAO.findBy(user));
        }
        return mav;
    }

    @RequestMapping("profile")
    public ModelAndView getProfile(HttpServletRequest request) {
        sessionUtil.setProfileRedirectPath(request, "/matchoffers");
        return new ModelAndView("redirect:/account/profile");
    }

    @RequestMapping("myoffers")
    public ModelAndView getMyOffers(HttpServletRequest request) {
        Player user = sessionUtil.getUser(request);
        List<MatchOffer> offers = matchOfferDAO.findBy(user);
        ModelAndView mav = new ModelAndView("matchoffers/myoffers", "Models", offers);
        return mav;
    }

    @RequestMapping(value = "add")
    public ModelAndView getEdit(HttpServletRequest request) {
        Player user = sessionUtil.getUser(request);
        if (user == null) {
            return getLoginRequiredView(request, msg.get("NewMatchOffer"));
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

    @RequestMapping(value = "{id}/edit")
    public ModelAndView getEdit(HttpServletRequest request, @PathVariable("id") Long id) {
        MatchOffer offer = matchOfferDAO.findById(id);
        return getEditView(offer);
    }

    @RequestMapping(value = {"add", "{id}/edit"}, method = POST)
    public ModelAndView postEdit(HttpServletRequest request, @ModelAttribute("Model") MatchOffer model, BindingResult result) {
        Player user = sessionUtil.getUser(request);
        if (user == null) {
            return getLoginRequiredView(request, msg.get("EditOffer"));
        }

        //ToDo: make sure court is bookable
        ModelAndView editView = getEditView(model);

        validator.validate(model, result);
        if (result.hasErrors()) {
            return editView;
        }

        try {
            model.setOwner(user);
            boolean isNewMatchOffer = model.getId() == null;
            matchOfferDAO.saveOrUpdate(model);

            //inform other users about new offers
            if (isNewMatchOffer) {
                List<Player> interestedPlayers = playerDAO.findPlayersInterestedIn(model);
                interestedPlayers.remove(user);
                if (!interestedPlayers.isEmpty()) {
                    Mail mail = new Mail();
                    mail.setSubject(msg.get("NewMatchOfferMailSubject"));
                    mail.setBody(getNewMatchOfferMailBody(request, model));
                    mail.setRecipients(Sets.newHashSet(interestedPlayers));
                    mailUtils.send(mail, request);
                }
            }
        } catch (MailException e) {
            LOG.error("Error while sending mails about new match offer: " + e.getMessage());
        }
        return new ModelAndView("redirect:/matchoffers/" + model.getId());
    }

    @RequestMapping(value = "{id}")
    public ModelAndView getShow(HttpServletRequest request, @PathVariable("id") Long id) {
        MatchOffer offer = matchOfferDAO.findById(id);
        return getShowView(request, offer);
    }

    @RequestMapping(value = "{id}", method = POST)
    public ModelAndView postOffer(HttpServletRequest request, @PathVariable("id") Long id) {
        Player user = sessionUtil.getUser(request);
        if (user == null) {
            return getLoginRequiredView(request, msg.get("EditOffer"));
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
        existingParticipantsEmail.setRecipients(offer.getPlayers());

        Mail newParticipantEmail = new Mail();
        newParticipantEmail.addRecipient(user);

        try {

            if (!StringUtils.isEmpty(acceptParticipation) && acceptParticipation.equals("on")) {
                //inform other players about new participant
                existingParticipantsEmail.setSubject(msg.get("MatchOfferNewParticipantEmailSubject"));

                //if the match is full
                if (offer.getPlayers().size() >= offer.getMaxPlayersCount()) {
                    //inform other players about new waiting list entry
                    existingParticipantsEmail.setBody(msg.get("MatchOfferNewWaitingListEntry", new Object[]{user.toString(), offer.toString(), offerURL, baseURL}));
                    mailUtils.send(existingParticipantsEmail, request);

                    //add user to waiting list
                    Set<Player> waitingList = offer.getWaitingList();
                    waitingList.add(user);
                    offer.setWaitingList(waitingList);
                    matchOfferDAO.saveOrUpdate(offer);

                    //inform new participant about waiting list
                    view.addObject("error", msg.get("MatchOfferWaitingList"));
                    return view;
                } else {
                    //inform other players about new participant
                    Integer remainingMinPlayersCount = offer.getMinPlayersCount() - offer.getPlayers().size() - 1;
                    String body;
                    if (remainingMinPlayersCount > 0) {
                        body = msg.get("MatchOfferNewParticipantEmailBody", new Object[]{user.toString(), offer.toString(), remainingMinPlayersCount, offerURL, baseURL});
                    } else {
                        body = msg.get("MatchOfferNewMatchEmailBody", new Object[]{user.toString(), offer.toString(), offerURL, baseURL + "/bookings", baseURL});
                    }
                    existingParticipantsEmail.setBody(body);

                    //inform new participant
                    newParticipantEmail.setSubject(msg.get("MatchOfferParticipationConfirmationEmailSubject"));
                    newParticipantEmail.setBody(msg.get("MatchOfferParticipationConfirmationEmailBody", new Object[]{user.toString(), offer.toString(), offerURL, baseURL}));

                    //add player to offer
                    Set<Player> players = offer.getPlayers();
                    players.add(user);
                    offer.setPlayers(players);

                    //if applicable remove player from waiting list
                    if (offer.getWaitingList().contains(user)) {
                        offer.getWaitingList().remove(user);
                    }

                    mailUtils.send(existingParticipantsEmail, request);
                    mailUtils.send(newParticipantEmail, request);
                }

                //confirm participation to user
                view.addObject("msg", msg.get("MatchOffersAcceptConfirmation"));

            }

            if (!StringUtils.isEmpty(cancelParticipation) && cancelParticipation.equals("on")) {
                //inform all players about cancellation
                existingParticipantsEmail.setSubject(msg.get("MatchOfferParticipantCancelledEmailSubject"));
                Integer remainingRequiredPlayersCount = offer.getMinPlayersCount() - offer.getPlayers().size() + 1;
                existingParticipantsEmail.setBody(msg.get("MatchOfferParticipantCancelledEmailBody", new Object[]{user.toString(), offer.toString(), remainingRequiredPlayersCount, offerURL, baseURL}));

                mailUtils.send(existingParticipantsEmail, request);

                //remove player from offer
                offer.getPlayers().remove(user);

                //confirm cancellation to user
                view.addObject("msg", msg.get("MatchOffersCancelConfirmation"));

                //inform waiting users
                for (Player waitingPlayer : offer.getWaitingList()) {
                    Mail waitingPlayerEmail = new Mail();
                    waitingPlayerEmail.addRecipient(waitingPlayer);
                    waitingPlayerEmail.setSubject(msg.get("MatchOfferWaitingListPlayerCancelledMailSubject"));
                    waitingPlayerEmail.setBody(msg.get("MatchOfferWaitingListPlayerCancelledMailBody", new Object[]{waitingPlayer, offer, offerURL, baseURL}));
                    mailUtils.send(waitingPlayerEmail, request);
                }
            }

            //persist changes
            matchOfferDAO.saveOrUpdate(offer);
        } catch (MailException e) {
            LOG.error(e);
            view.addObject("error", msg.get("FailedToSendEmail"));
            return view;
        }
        return view;
    }

    @Override
    public ModelAndView getDelete(HttpServletRequest request, @PathVariable("id") Long id) {
        Player user = sessionUtil.getUser(request);
        if (user == null) {
            return getLoginRequiredView(request, msg.get("EditMatchOffer"));
        }
        MatchOffer model = matchOfferDAO.findById(id);
        if (model == null || !model.getOwner().equals(user)) {
            return redirectToIndex(request);
        }
        return getDeleteView(model);
    }

    @Override
    public ModelAndView postDelete(HttpServletRequest request, @PathVariable("id") Long id) {
        Player user = sessionUtil.getUser(request);
        if (user == null) {
            return getLoginRequiredView(request, msg.get("EditMatchOffer"));
        }
        MatchOffer model = matchOfferDAO.findById(id);
        if (model == null || !model.getOwner().equals(user)) {
            return redirectToIndex(request);
        }
        try {
            getDAO().deleteById(id);
        } catch (DataIntegrityViolationException e) {
            LOG.warn("Attempt to delete " + model + " failed due to " + e);
            ModelAndView deleteView = getDeleteView(model);
            deleteView.addObject("error", msg.get("CannotDeleteDueToRefrence", new Object[]{model.toString()}));
            return deleteView;
        }
        return redirectToIndex(request);
    }

    private ModelAndView getEditView(MatchOffer model) {
        ModelAndView mav = new ModelAndView("matchoffers/edit", "Model", model);
        mav.addObject("SkillLevels", SkillLevel.values());
        return mav;
    }

    private ModelAndView getShowView(HttpServletRequest request, MatchOffer model) {
        LocalDate today = new LocalDate();
        LocalTime now = new LocalTime(Constants.DEFAULT_TIMEZONE);
        if (model == null) {
            return new ModelAndView("matchoffers/invalid");
        } else if (model.getStartDate().isBefore(today) || (model.getStartDate().equals(today) && model.getStartTime().isBefore(now))) {
            return new ModelAndView("matchoffers/expired", "Model", model);
        } else {
            ModelAndView mav = new ModelAndView("matchoffers/offer", "Model", model);
            mav.addObject("OfferURL", getOfferURL(request, model));
            mav.addObject("NewMatchOfferMailSubject", msg.get("NewMatchOfferMailSubject"));
            mav.addObject("NewMatchOfferMailBody", getNewMatchOfferMailBody(request, model).replace("\n", "%0A"));
            mav.addObject("PageTitle", String.format("%s %s", FormatUtils.DATE_WITH_DAY.print(model.getStartDate()), FormatUtils.TIME_HUMAN_READABLE.print(model.getStartTime())));
            return mav;
        }
    }

    @Override
    public BaseEntityDAOI getDAO() {
        return matchOfferDAO;
    }

    @Override
    public String getModuleName() {
        return "matchoffers";
    }

    private String getNewMatchOfferMailBody(HttpServletRequest request, MatchOffer model) {
        Object[] params = new Object[7];
        String baseURL = RequestUtil.getBaseURL(request);
        params[0] = model.getOwner();
        params[1] = model.getStartDate().toString(FormatUtils.DATE_HUMAN_READABLE);
        params[2] = model.getStartTime().toString(FormatUtils.TIME_HUMAN_READABLE);
        params[3] = StringUtils.join(model.getPlayers(), ", ");
        params[4] = baseURL + "/matchoffers/" + model.getId();
        params[5] = baseURL + "/account/profile";
        params[6] = baseURL;
        return msg.get("NewMatchOfferMailBody", params);
    }

    private String getOfferURL(HttpServletRequest request, MatchOffer offer) {
        return RequestUtil.getBaseURL(request) + "/matchoffers/" + offer.getId();
    }
}
