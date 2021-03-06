/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.appsolve.padelcampus.controller;

import de.appsolve.padelcampus.constants.Constants;
import de.appsolve.padelcampus.data.Credentials;
import de.appsolve.padelcampus.data.Mail;
import de.appsolve.padelcampus.db.dao.EventDAOI;
import de.appsolve.padelcampus.db.dao.PlayerDAOI;
import de.appsolve.padelcampus.db.model.Contact;
import de.appsolve.padelcampus.db.model.Player;
import de.appsolve.padelcampus.exceptions.MailException;
import de.appsolve.padelcampus.utils.LoginUtil;
import de.appsolve.padelcampus.utils.PlayerUtil;
import de.appsolve.padelcampus.utils.RequestUtil;
import de.appsolve.padelcampus.utils.SessionUtil;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.UUID;

import static de.appsolve.padelcampus.constants.Constants.PATH_HOME;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author dominik
 */
@Controller()
@RequestMapping("/login")
public class LoginController extends BaseController {

    private static final Logger LOG = Logger.getLogger(LoginController.class);

    @Autowired
    PlayerDAOI playerDAO;

    @Autowired
    EventDAOI eventDAO;

    @Autowired
    SessionUtil sessionUtil;

    @Autowired
    PlayerUtil playerUtil;

    @Autowired
    LoginUtil loginUtil;

    @RequestMapping(value = {"", "pre-register"})
    public ModelAndView showLogin(HttpServletRequest request) {
        ModelAndView mav = getLoginView(new Credentials(), request);
        return mav;
    }

    @RequestMapping(method = POST)
    public ModelAndView doLogin(
            @Valid @ModelAttribute("Model") Credentials credentials,
            @RequestParam(value = "stay-logged-in", defaultValue = "false", required = false) Boolean stayLoggedIn,
            BindingResult bindingResult,
            HttpServletRequest request,
            HttpServletResponse response) {
        ModelAndView loginView = getLoginView(credentials, request);
        try {
            doLogin(credentials, stayLoggedIn, request, response);
        } catch (Exception e) {
            bindingResult.addError(new ObjectError("email", e.getMessage()));
            return loginView;
        }

        String redirectPath = sessionUtil.getLoginRedirectPath(request) == null ? "/" + PATH_HOME : sessionUtil.getLoginRedirectPath(request);
        sessionUtil.setLoginRedirectPath(request, null);
        return new ModelAndView("redirect:" + redirectPath);
    }

    @RequestMapping(value = "pre-register", method = POST)
    public ModelAndView doPreRegister(@Valid @ModelAttribute("Model") Credentials credentials, BindingResult bindingResult, HttpServletRequest request) {
        ModelAndView loginView = getLoginView(credentials, request);
        if (bindingResult.hasErrors()) {
            return loginView;
        }
        try {
            Player player = playerDAO.findByEmail(credentials.getEmail());
            if (player == null) {
                player = new Player();
            } else if (player.getDeleted()) {
                throw new Exception(msg.get("CannotUseDeletedAccountEmail", new Object[]{player.getEmail()}));
            } else if (!player.getGuest()) {
                throw new Exception(msg.get("EmailAlreadyRegistered"));
            }
            player.setEmail(credentials.getEmail());
            player.setPassword(credentials.getPassword());
            return getRegisterView(player, request, Boolean.FALSE, Boolean.FALSE);
        } catch (Exception e) {
            bindingResult.addError(new ObjectError("email", e.getMessage()));
            return loginView;
        }
    }

    @RequestMapping(value = "register")
    public ModelAndView getRegister(HttpServletRequest request) {
        return getRegisterView(new Player(), request, Boolean.FALSE, Boolean.FALSE);
    }

    @RequestMapping(value = "register", method = POST)
    public ModelAndView doRegister(
            @Valid @ModelAttribute("Model") Player player,
            BindingResult bindingResult,
            @RequestParam(value = "stay-logged-in", defaultValue = "false", required = false) Boolean stayLoggedIn,
            @RequestParam(value = "accept-tac", defaultValue = "false", required = false) Boolean acceptTAC,
            @RequestParam(value = "accept-pp", defaultValue = "false", required = false) Boolean acceptPP,
            HttpServletRequest request,
            HttpServletResponse response) {
        ModelAndView mav = getRegisterView(player, request, acceptTAC, acceptPP);
        if (bindingResult.hasErrors()) {
            return mav;
        }
        try {
            if (!acceptTAC) {
                throw new Exception(msg.get("PleaseAcceptTAC"));
            }

            if (!acceptPP) {
                throw new Exception(msg.get("PleaseAcceptPP"));
            }

            if (StringUtils.isEmpty(player.getPassword())) {
                throw new Exception(msg.get("PasswordMayNotBeEmpty"));
            }

            if (player.getDeleted()) {
                throw new Exception(msg.get("CannotUseDeletedAccountEmail", new Object[]{player.getEmail()}));
            }

            Player persistedPlayer = playerDAO.findByEmail(player.getEmail());
            if (persistedPlayer != null && !StringUtils.isEmpty(persistedPlayer.getPasswordHash())) {
                throw new Exception(msg.get("EmailAlreadyRegistered"));
            }

            Player playerToPersist;
            if (persistedPlayer == null) {
                playerToPersist = player;
            } else {
                //update existing player instead of generating a new one
                playerToPersist = persistedPlayer;
                playerToPersist.setFirstName(player.getFirstName());
                playerToPersist.setLastName(player.getLastName());
                playerToPersist.setPhone(player.getPhone());
                playerToPersist.setGender(player.getGender());
                playerToPersist.setPassword(player.getPassword());
                playerToPersist.setAllowEmailContact(player.getAllowEmailContact());
            }

            //create player object which also generates a UUID
            playerToPersist = playerDAO.saveOrUpdate(playerToPersist);

            String accountVerificationLink = PlayerUtil.getAccountVerificationLink(request, playerToPersist);

            Mail mail = new Mail();
            mail.addRecipient(player);
            mail.setSubject(msg.get("RegistrationMailSubject"));
            String key = player.getAllowEmailContact() ? "RegistrationMailBody2" : "RegistrationMailBody";
            mail.setBody(StringEscapeUtils.unescapeJava(msg.get(key, new Object[]{playerToPersist.toString(), accountVerificationLink, RequestUtil.getBaseURL(request)})));
            try {
                mailUtils.send(mail, request);
            } catch (MailException e) {
                LOG.error(e.getMessage(), e);
            }

            //login user
            sessionUtil.setUser(request, playerToPersist);

            //set auto login cookie if user has requested so
            setLoginCookie(stayLoggedIn, request, response);

            String redirectPath = sessionUtil.getLoginRedirectPath(request);
            if (!StringUtils.isEmpty(redirectPath)) {
                sessionUtil.setLoginRedirectPath(request, null);
                return new ModelAndView("redirect:" + redirectPath);
            }
            return getRegisterSuccessView();
        } catch (Exception e) {
            LOG.warn("Error while registering user", e);
            bindingResult.addError(new ObjectError("id", e.getMessage()));
            return mav;
        }
    }

    @RequestMapping(value = "confirm/{UUID}")
    public ModelAndView confirmEmail(HttpServletRequest request, @PathVariable("UUID") String UUID) {
        Player player = playerDAO.findByUUID(UUID);
        player.setVerified(true);
        playerDAO.saveOrUpdate(player);
        sessionUtil.setUser(request, player);
        ModelAndView mav = new ModelAndView("login/confirm-email");
        return mav;
    }

    @RequestMapping(value = "forgot-password", method = GET)
    public ModelAndView forgotPassowrd() {
        return getForgotPasswordView(new Credentials());
    }

    @RequestMapping(value = "forgot-password", method = POST)
    public ModelAndView forgotPassowrd(@Valid @ModelAttribute("Model") Credentials credentials, BindingResult bindingResult, HttpServletRequest request) {
        ModelAndView forgotPasswordView = getForgotPasswordView(credentials);
        if (bindingResult.hasErrors()) {
            return forgotPasswordView;
        }
        try {
            if (StringUtils.isEmpty(credentials.getEmail())) {
                throw new Exception(msg.get("EmailMayNotBeEmpty"));
            }

            Player player = playerDAO.findByEmail(credentials.getEmail());
            if (player == null) {
                throw new Exception(msg.get("EmailAddressNotRegistered"));
            }
            if (player.getDeleted()) {
                throw new Exception(msg.get("CannotUseDeletedAccountEmail", new Object[]{credentials.getEmail()}));
            }

            UUID randomUUID = UUID.randomUUID();
            String resetPasswordURL = RequestUtil.getBaseURL(request) + "/login/reset-password/" + randomUUID.toString();

            player.setPasswordResetUUID(randomUUID.toString());
            DateTime expiryDate = new DateTime(Constants.DEFAULT_TIMEZONE).plusDays(1);
            player.setPasswordResetExpiryDate(expiryDate);
            playerDAO.saveOrUpdate(player);

            Mail mail = new Mail();
            Contact contact = new Contact();
            contact.setEmailAddress(player.getEmail());
            contact.setEmailDisplayName(player.toString());
            mail.addRecipient(contact);
            mail.setSubject(msg.get("ForgotPasswordMailSubject"));
            mail.setBody(StringEscapeUtils.unescapeJava(msg.get("ForgotPasswordMailBody", new Object[]{player.toString(), resetPasswordURL, RequestUtil.getBaseURL(request)})));
            mailUtils.send(mail, request);
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
            bindingResult.addError(new ObjectError("email", e.getMessage()));
            return forgotPasswordView;
        }
        return new ModelAndView("login/forgot-password-success", "Email", credentials.getEmail());
    }

    @RequestMapping(value = "reset-password/{UUID}", method = GET)
    public ModelAndView resetPassword(@PathVariable("UUID") String UUID) {
        Player player = playerDAO.findByPasswordResetUUID(UUID);

        if (player == null) {
            return new ModelAndView("login/reset-password-link-invalid");
        }

        Credentials credentials = new Credentials();
        credentials.setEmail(player.getEmail());
        return getResetPasswordView(credentials);
    }

    @RequestMapping(value = "reset-password/{UUID}", method = POST)
    public ModelAndView resetPassword(@Valid @ModelAttribute("Model") Credentials credentials, BindingResult bindingResult, @PathVariable("UUID") String UUID) {
        ModelAndView mav = getResetPasswordView(credentials);
        Player player = playerDAO.findByPasswordResetUUID(UUID);

        if (!player.getEmail().equals(credentials.getEmail())) {
            bindingResult.addError(new ObjectError("email", "nice try"));
            return mav;
        }

        if (player.getPasswordResetExpiryDate().isBeforeNow()) {
            bindingResult.addError(new ObjectError("email", msg.get("PasswordResetLinkExpired")));
            return mav;
        }

        if (StringUtils.isEmpty(credentials.getPassword())) {
            bindingResult.addError(new ObjectError("email", msg.get("PasswordMayNotBeEmpty")));
            return mav;
        }
        player.setPassword(credentials.getPassword());
        playerDAO.saveOrUpdate(player);
        return new ModelAndView("login/reset-password-success");
    }


    private ModelAndView getLoginView(Credentials credentials, HttpServletRequest request) {
        checkForRedirectParam(request);
        ModelAndView mav = new ModelAndView("login/login", "Model", credentials);
        return mav;
    }

    private ModelAndView getRegisterView(Player player, HttpServletRequest request, Boolean acceptTAC, Boolean acceptPP) {
        checkForRedirectParam(request);
        ModelAndView mav = new ModelAndView("login/register");
        mav.addObject("Model", player);
        mav.addObject("AcceptTAC", acceptTAC);
        mav.addObject("AcceptPP", acceptPP);
        return mav;
    }

    private ModelAndView getRegisterSuccessView() {
        return new ModelAndView("login/register-success");
    }

    private ModelAndView getForgotPasswordView(Credentials credentials) {
        return new ModelAndView("login/forgot-password", "Model", credentials);
    }

    private ModelAndView getResetPasswordView(Credentials credentials) {
        return new ModelAndView("login/reset-password", "Model", credentials);
    }


    private Player doLogin(Credentials credentials, Boolean stayLoggedIn, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (StringUtils.isEmpty(credentials.getEmail())) {
            throw new Exception(msg.get("EmailMayNotBeEmpty"));
        }
        if (StringUtils.isEmpty(credentials.getPassword())) {
            throw new Exception(msg.get("PasswordMayNotBeEmpty"));
        }

        Player player = playerDAO.findByEmail(credentials.getEmail());

        if (player.getDeleted()) {
            throw new Exception(msg.get("CannotUseDeletedAccountEmail", new Object[]{credentials.getEmail()}));
        }

        if (!playerUtil.isPasswordValid(player, credentials.getPassword())) {
            throw new Exception(msg.get("UnknownEmailAddressOrPassword"));
        }
        if (!player.getSalted()) {
            player.setPassword(credentials.getPassword());
            playerDAO.saveOrUpdate(player);
        }

        sessionUtil.setUser(request, player);
        setLoginCookie(stayLoggedIn, request, response);
        return player;
    }

    private void setLoginCookie(Boolean stayLoggedIn, HttpServletRequest request, HttpServletResponse response) {
        if (stayLoggedIn != null && stayLoggedIn) {
            loginUtil.updateLoginCookie(request, response);
        } else {
            loginUtil.deleteLoginCookie(request, response);
        }
    }

    private void checkForRedirectParam(HttpServletRequest request) {
        String redirectPath = request.getParameter("redirect");
        if (!StringUtils.isEmpty(redirectPath) && !redirectPath.contains("://")) {
            sessionUtil.setLoginRedirectPath(request, redirectPath);
        }
    }
}
