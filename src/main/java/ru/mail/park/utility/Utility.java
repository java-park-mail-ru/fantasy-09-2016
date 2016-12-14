package ru.mail.park.utility;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import ru.mail.park.model.UserProfile;

import java.util.List;
import java.util.Random;

public final class Utility {

    public static final Random RANDOM = new Random();

    public static final String EMPTY_RESPONSE = "{}";

    public static String object2JSON(Object o) {
        String result;
        try {
            result = new ObjectMapper().writeValueAsString(o);
        } catch (JsonProcessingException e) {
            result = "";
        }
        return result;
    }

    public enum FailedResponse {
        EMPTY_FIELDS_IN_REQUEST(0, "empty fields in request"), // 0x -- request errors
        WRONG_ORDER_TYPE(1, "wrong order type"),
        WRONG_OFFSET(2, "wrong offset"),

        AUTH_REQUIRED(10, "auth required"), // 1x -- auth errors
        AUTH_FAILED(11, "auth failed"),
        ALREADY_AUTHORIZED(12, "already authorized"),

        USER_ALREADY_EXITS(20, "user already exists"), // 2x -- user errors
        USER_NOT_EXIST(21, "user not exist");

        private final ResponseEntity<FailedResponseBody> response;

        FailedResponse(int code, String description) {
            this.response = ResponseEntity.badRequest().body(new FailedResponseBody(code, description));
        }

        public ResponseEntity<FailedResponseBody> getResponse() {
            return response;
        }
    }

    public static final class RegistrationRequest {
        private final String login;
        private final String password;
        private final String email;

        @JsonCreator
        public RegistrationRequest(@JsonProperty("login") String login,
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

    public static final class LoginRequest {
        private final String login;
        private final String password;

        @JsonCreator
        public LoginRequest(@JsonProperty("login") String login,
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

    public static final class ChangeUserRequest {
        private final String email;

        @JsonCreator
        public ChangeUserRequest(@JsonProperty("email") String email) {
            this.email = email;
        }

        public String getEmail() {
            return email;
        }
    }

    public static final class ChangePasswordRequest {
        private final String oldPassword;
        private final String newPassword;

        @JsonCreator
        private ChangePasswordRequest(@JsonProperty("old_password") String oldPassword,
                                      @JsonProperty("new_password") String newPassword) {
            this.oldPassword = oldPassword;
            this.newPassword = newPassword;
        }

        public String getOldPassword() {
            return oldPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }
    }

    public static final class FailedResponseBody {
        private final int error;
        private final String description;

        private FailedResponseBody(int error, String description) {
            this.error = error;
            this.description = description;
        }

        @SuppressWarnings("unused")
        public int getError() {
            return error;
        }

        @SuppressWarnings("unused")
        public String getDescription() {
            return description;
        }
    }

    public static final class UserDataBody {
        private final String login;
        private final String email;

        public UserDataBody(UserProfile up) {
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

    public static final class ListUsersBody {
        private final UserDataBody[] users;

        public ListUsersBody(List<UserProfile> users) {
            this.users = users.stream()
                    .map(UserDataBody::new)
                    .toArray(UserDataBody[]::new);
        }

        @SuppressWarnings("unused")
        public UserDataBody[] getUsers() {
            return users;
        }
    }

    public static final class UserAlreadyExistException extends Exception {
        public UserAlreadyExistException(Throwable cause) {
            super(cause);
        }
    }

    public static final class WebSocketIOException extends Exception {
        public WebSocketIOException(Throwable cause) {
            super(cause);
        }
    }
}
