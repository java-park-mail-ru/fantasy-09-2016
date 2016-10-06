package ru.mail.park.main;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.mail.park.model.UserProfile;
import ru.mail.park.services.AccountService;

import javax.servlet.http.HttpSession;

@RestController
public class RegistrationController {

    private static final String EMPTY_RESPONSE = "{}";

    private final AccountService accountService;

    @Autowired
    public RegistrationController(AccountService accountService) {
        this.accountService = accountService;
    }

    @RequestMapping(path = "/api/user", method = RequestMethod.POST)
    public ResponseEntity createUser(@RequestBody RegistrationRequest body,
                                     HttpSession httpSession) {
        final String login = body.getLogin();
        final String password = body.getPassword();
        final String email = body.getEmail();

        if (StringUtils.isEmpty(login) || StringUtils.isEmpty(password) || StringUtils.isEmpty(email)) {
            return FailedResponseCode.EMPTY_FIELDS_IN_REQUEST.response;
        }

        final UserProfile existingUser = accountService.getUser(login);

        if (existingUser != null) {
            return FailedResponseCode.USER_ALREADY_EXITS.response;
        }

        accountService.addUser(login, password, email);
        httpSession.setAttribute("login", login);

        return ResponseEntity.ok(EMPTY_RESPONSE);
    }

    @RequestMapping(path = "/api/user", method = RequestMethod.GET)
    public ResponseEntity getUser(@RequestParam String login,
                                  HttpSession httpSession) {

        final String sessLogin = (String) httpSession.getAttribute("login");
        if (sessLogin == null) {
            return FailedResponseCode.AUTH_REQUIRED.response;
        }

        if (StringUtils.isEmpty(login)) {
            return FailedResponseCode.EMPTY_FIELDS_IN_REQUEST.response;
        }

        final UserProfile up = accountService.getUser(login);

        if (up == null) {
            return FailedResponseCode.USER_NOT_EXIST.response;
        }

        return ResponseEntity.ok(new UserDataResponse(up));
    }

    @RequestMapping(path = "/api/user", method = RequestMethod.DELETE)
    public ResponseEntity deleteUser(HttpSession httpSession) {

        final String login = (String) httpSession.getAttribute("login");
        if (login == null) {
            return FailedResponseCode.AUTH_REQUIRED.response;
        }

        accountService.deleteUser(login);
        httpSession.removeAttribute("login");

        return ResponseEntity.ok(EMPTY_RESPONSE);
    }

    @RequestMapping(path = "/api/session", method = RequestMethod.POST)
    public ResponseEntity login(@RequestBody LoginRequest body, HttpSession httpSession) {

        final String login = body.getLogin();
        final String password = body.getPassword();

        if (StringUtils.isEmpty(login) || StringUtils.isEmpty(password)) {
            return FailedResponseCode.EMPTY_FIELDS_IN_REQUEST.response;
        }

        final String sessionLogin = (String) httpSession.getAttribute("login");

        if (sessionLogin != null) {
            return FailedResponseCode.ALREADY_AUTHORIZED.response;
        }

        final UserProfile user = accountService.getUser(login);

        if (user == null || !user.getPassword().equals(password)) {
            return FailedResponseCode.AUTH_FAILED.response;
        }

        httpSession.setAttribute("login", login);

        return ResponseEntity.ok(EMPTY_RESPONSE);
    }

    @RequestMapping(path = "/api/session", method = RequestMethod.DELETE)
    public ResponseEntity logout(HttpSession httpSession) {

        final String login = (String) httpSession.getAttribute("login");
        if (login == null) {
            return FailedResponseCode.AUTH_REQUIRED.response;
        }

        httpSession.removeAttribute("login");

        return ResponseEntity.ok(EMPTY_RESPONSE);
    }

    private enum FailedResponseCode {
        EMPTY_FIELDS_IN_REQUEST(0),
        AUTH_REQUIRED(1),
        AUTH_FAILED(2),
        USER_ALREADY_EXITS(3),
        USER_NOT_EXIST(4),
        ALREADY_AUTHORIZED(5);

        private final ResponseEntity<FailedResponse> response;

        FailedResponseCode(int code) {
            this.response = ResponseEntity.badRequest().body(new FailedResponse(code));
        }
    }

    private static final class RegistrationRequest {
        private final String login;
        private final String password;
        private final String email;

        @JsonCreator
        private RegistrationRequest(@JsonProperty("login") String login,
                                    @JsonProperty("password") String password,
                                    @JsonProperty("email") String email) {
            this.login = login;
            this.password = password;
            this.email = email;
        }

        public String getLogin() {
            return login;
        }

        public String getPassword() {
            return password;
        }

        public String getEmail() {
            return email;
        }
    }

    private static final class LoginRequest {
        private final String login;
        private final String password;

        @JsonCreator
        private LoginRequest(@JsonProperty("login") String login,
                             @JsonProperty("password") String password) {
            this.login = login;
            this.password = password;
        }

        public String getLogin() {
            return login;
        }

        public String getPassword() {
            return password;
        }
    }

    private static final class FailedResponse {
        private final int error;

        private FailedResponse(int error) {
            this.error = error;
        }

        @SuppressWarnings("unused")
        public int getError() {
            return error;
        }
    }

    private static final class UserDataResponse {
        private final String login;
        private final String email;

        private UserDataResponse(UserProfile up) {
            this.login = up.getLogin();
            this.email = up.getEmail();
        }

        @SuppressWarnings("unused")
        public String getLogin() {
            return login;
        }

        @SuppressWarnings("unused")
        public String getEmail() {
            return email;
        }
    }
}