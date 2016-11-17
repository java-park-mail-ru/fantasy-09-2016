package ru.mail.park.main;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.mail.park.model.UserProfile;
import ru.mail.park.services.IAccountService;

import javax.servlet.http.HttpSession;

import static ru.mail.park.utility.Utility.*;

@RestController
public class SessionController {

    private final IAccountService accountService;

    @Autowired
    public SessionController(IAccountService accountService) {
        this.accountService = accountService;
    }


    @RequestMapping(path = "/api/session", method = RequestMethod.POST)
    public ResponseEntity login(@RequestBody LoginRequest body, HttpSession httpSession) {

        final String login = body.getLogin();
        final String password = body.getPassword();

        if (StringUtils.isEmpty(login) || StringUtils.isEmpty(password)) {
            return FailedResponse.EMPTY_FIELDS_IN_REQUEST.getResponse();
        }

        final String sessionLogin = (String) httpSession.getAttribute("login");

        if (sessionLogin != null) {
            return FailedResponse.ALREADY_AUTHORIZED.getResponse();
        }

        final UserProfile user = accountService.getUser(login);

        if (user == null || !user.getPassword().equals(password)) {
            return FailedResponse.AUTH_FAILED.getResponse();
        }

        httpSession.setAttribute("login", login);

        return ResponseEntity.ok(EMPTY_RESPONSE);
    }

    @RequestMapping(path = "/api/session", method = RequestMethod.DELETE)
    public ResponseEntity logout(HttpSession httpSession) {

        final String login = (String) httpSession.getAttribute("login");
        if (login == null) {
            return FailedResponse.AUTH_REQUIRED.getResponse();
        }

        httpSession.removeAttribute("login");

        return ResponseEntity.ok(EMPTY_RESPONSE);
    }

    @RequestMapping(path = "/api/session", method = RequestMethod.GET)
    public ResponseEntity info(HttpSession httpSession) {

        final String login = (String) httpSession.getAttribute("login");
        if (login == null) {
            return FailedResponse.AUTH_REQUIRED.getResponse();
        }

        return ResponseEntity.ok(new UserDataBody(accountService.getUser(login)));
    }

}
