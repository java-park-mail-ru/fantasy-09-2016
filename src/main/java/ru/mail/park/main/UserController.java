package ru.mail.park.main;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.mail.park.model.UserProfile;
import ru.mail.park.services.AccountService;

import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Comparator;

import static ru.mail.park.utility.Utility.*;

@RestController
public class UserController {

    private final AccountService accountService;

    @Autowired
    public UserController(AccountService accountService) {
        this.accountService = accountService;
    }

    @RequestMapping(path = "/api/user", method = RequestMethod.POST)
    public ResponseEntity addUser(@RequestBody RegistrationRequest body) {
        final String login = body.getLogin();
        final String password = body.getPassword();
        final String email = body.getEmail();

        if (StringUtils.isEmpty(login) || StringUtils.isEmpty(password) || StringUtils.isEmpty(email)) {
            return FailedResponse.EMPTY_FIELDS_IN_REQUEST.getResponse();
        }

        final UserProfile existingUser = accountService.getUser(login);

        if (existingUser != null) {
            return FailedResponse.USER_ALREADY_EXITS.getResponse();
        }

        accountService.addUser(login, password, email);

        return ResponseEntity.ok(EMPTY_RESPONSE);
    }

    @RequestMapping(path = "/api/user", method = RequestMethod.GET)
    public ResponseEntity listUsers(@RequestParam(required = false) String order,
                                    @RequestParam(required = false) String offset,
                                    HttpSession httpSession) {

        final String sessLogin = (String) httpSession.getAttribute("login");
        if (sessLogin == null) {
            return FailedResponse.AUTH_REQUIRED.getResponse();
        }

        UserProfile[] users = accountService.listUsers();

        if (!StringUtils.isEmpty(order)) {
            final Comparator<UserProfile> c;
            switch (order) {
                case "asc":
                    c = (u1, u2) -> u1.getLogin().compareTo(u2.getLogin());
                    break;
                case "desc":
                    c = (u1, u2) -> -u1.getLogin().compareTo(u2.getLogin());
                    break;
                default:
                    return FailedResponse.WRONG_ORDER_TYPE.getResponse();
            }
            Arrays.sort(users, c);
        }

        if (!StringUtils.isEmpty(offset)) {
            final int intOffset;
            try {
                intOffset = Integer.parseInt(offset);
            } catch (NumberFormatException e) {
                return FailedResponse.WRONG_OFFSET.getResponse();
            }
            if (intOffset < 0 || intOffset >= users.length) {
                return FailedResponse.WRONG_OFFSET.getResponse();
            }
            final UserProfile[] userSlice = new UserProfile[users.length - intOffset];
            System.arraycopy(users, intOffset, userSlice, 0, users.length - intOffset);
            users = userSlice;
        }

        return ResponseEntity.ok(new ListUsersBody(users));
    }

    @RequestMapping(path = "/api/user/{login}", method = RequestMethod.GET)
    public ResponseEntity getUser(@PathVariable(name = "login") String login,
                                  HttpSession httpSession) {

        final String sessLogin = (String) httpSession.getAttribute("login");
        if (sessLogin == null) {
            return FailedResponse.AUTH_REQUIRED.getResponse();
        }

        if (StringUtils.isEmpty(login)) {
            return FailedResponse.EMPTY_FIELDS_IN_REQUEST.getResponse();
        }

        final UserProfile up = accountService.getUser(login);

        if (up == null) {
            return FailedResponse.USER_NOT_EXIST.getResponse();
        }

        return ResponseEntity.ok(new UserDataBody(up));
    }

    @RequestMapping(path = "/api/user", method = RequestMethod.PUT)
    public ResponseEntity putUser(@RequestBody ChangeUserRequest body,
                                  HttpSession httpSession) {

        final String sessLogin = (String) httpSession.getAttribute("login");
        if (sessLogin == null) {
            return FailedResponse.AUTH_REQUIRED.getResponse();
        }

        final String email = body.getEmail();
        final String avatar = body.getAvatar();

        if (StringUtils.isEmpty(email) || StringUtils.isEmpty(avatar)) {
            return FailedResponse.EMPTY_FIELDS_IN_REQUEST.getResponse();
        }


        final UserProfile up = accountService.getUser(sessLogin);

        up.setEmail(email);
        up.setAvatar(avatar);

        return ResponseEntity.ok(EMPTY_RESPONSE);
    }

    @RequestMapping(path = "/api/user/password", method = RequestMethod.PUT)
    public ResponseEntity putUserPassword(@RequestBody ChangePasswordRequest body,
                                          HttpSession httpSession) {

        final String sessLogin = (String) httpSession.getAttribute("login");
        if (sessLogin == null) {
            return FailedResponse.AUTH_REQUIRED.getResponse();
        }

        final String oldPassword = body.getOldPassword();
        final String newPassword = body.getNewPassword();

        if (StringUtils.isEmpty(oldPassword) || StringUtils.isEmpty(newPassword)) {
            return FailedResponse.EMPTY_FIELDS_IN_REQUEST.getResponse();
        }

        final UserProfile up = accountService.getUser(sessLogin);

        if (!up.getPassword().equals(oldPassword)) {
            return FailedResponse.AUTH_FAILED.getResponse();
        }

        up.setPassword(newPassword);

        return ResponseEntity.ok(EMPTY_RESPONSE);
    }
}