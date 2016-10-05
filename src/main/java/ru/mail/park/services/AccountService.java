package ru.mail.park.services;

import org.springframework.stereotype.Service;
import ru.mail.park.model.UserProfile;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class AccountService {
    private final ConcurrentHashMap<String, UserProfile> registeredUsers = new ConcurrentHashMap<>();


    public void addUser(String login, String password, String email) {
        registeredUsers.put(login, new UserProfile(login, password, email));
    }

    public UserProfile getUser(String login) {
        return registeredUsers.get(login);
    }

    public void deleteUser(String login) {
        registeredUsers.remove(login);
    }
}
