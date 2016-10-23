package ru.mail.park.model;

public class UserProfile {
    private final String login;
    private String password;
    private String email;
    private String avatar;

    public UserProfile(String login, String password, String email) {
        this.login = login;
        this.password = password;
        this.email = email;
        this.avatar = null;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
