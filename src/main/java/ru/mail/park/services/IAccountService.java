package ru.mail.park.services;

import ru.mail.park.model.UserProfile;
import ru.mail.park.utility.Utility;

import java.util.List;

public interface IAccountService {

    void createUser(String login, String password, String email) throws Utility.UserAlreadyExistException;

    UserProfile getUser(String login);

    void changePassword(String login, String newPassword);

    void setDetails(String login, String email);

    List<UserProfile> listUsers(boolean order, int offset);
}
