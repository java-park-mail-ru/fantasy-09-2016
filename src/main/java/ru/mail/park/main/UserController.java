package ru.mail.park.main;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.mail.park.model.UserProfile;
import ru.mail.park.services.IAccountService;

import javax.servlet.http.HttpSession;
import java.util.List;

import static ru.mail.park.utility.Utility.*;

@RestController
public class UserController {

    private final IAccountService accountService;

    @Autowired
    public UserController(IAccountService accountService) {
        this.accountService = accountService;
    }

    @RequestMapping(path = "/api/user", method = RequestMethod.POST)
    public ResponseEntity createUser(@RequestBody RegistrationRequest body) {
        final String login = body.getLogin();
        final String password = body.getPassword();
        final String email = body.getEmail();

        if (StringUtils.isEmpty(login) || StringUtils.isEmpty(password) || StringUtils.isEmpty(email)) {
            return FailedResponse.EMPTY_FIELDS_IN_REQUEST.getResponse();
        }

        try {
            accountService.createUser(login, password, email);
        } catch (UserAlreadyExistException e) {
            return FailedResponse.USER_ALREADY_EXITS.getResponse();
        }
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

        boolean boolOrder = true;

        if (!StringUtils.isEmpty(order)) {
            if (!order.equals("asc") && !order.equals("desc")) {
                return FailedResponse.WRONG_ORDER_TYPE.getResponse();
            }
            boolOrder = order.equals("asc");
        }

        int intOffset = 0;

        if (!StringUtils.isEmpty(offset)) {
            try {
                intOffset = Integer.parseInt(offset);
            } catch (NumberFormatException e) {
                return FailedResponse.WRONG_OFFSET.getResponse();
            }
            if (intOffset < 0) {
                return FailedResponse.WRONG_OFFSET.getResponse();
            }
        }

        final List<UserProfile> users = accountService.listUsers(boolOrder, intOffset);

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

        if (StringUtils.isEmpty(email)) {
            return FailedResponse.EMPTY_FIELDS_IN_REQUEST.getResponse();
        }

        accountService.setDetails(sessLogin, email);

        return ResponseEntity.ok(EMPTY_RESPONSE);
    }

    @RequestMapping(path = "/api/password", method = RequestMethod.PUT)
    public ResponseEntity changePassword(@RequestBody ChangePasswordRequest body,
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

        accountService.changePassword(sessLogin, newPassword);

        return ResponseEntity.ok(EMPTY_RESPONSE);
    }
}