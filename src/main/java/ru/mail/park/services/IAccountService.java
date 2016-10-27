package ru.mail.park.services;

import ru.mail.park.model.UserProfile;

import java.util.List;

public interface IAccountService {

    void createUser(String login, String password, String email);

    UserProfile getUser(String login);

    void changePassword(String login, String newPassword);

    void setDetails(String login, String email);

    List<UserProfile> listUsers(boolean order, int offset);
}
