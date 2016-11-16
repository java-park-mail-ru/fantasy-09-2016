package ru.mail.park.services;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mail.park.model.UserProfile;
import ru.mail.park.utility.Utility;

import java.util.List;

@Service
@Transactional
public class AccountService implements IAccountService {
    private static final RowMapper<UserProfile> USER_MAPPER =
            (res, rowNum) -> new UserProfile(res.getString(1), res.getString(2), res.getString(3));
    private final JdbcTemplate template;

    public AccountService(JdbcTemplate template) {
        this.template = template;
    }

    @Override
    public void createUser(String login, String password, String email) throws Utility.UserAlreadyExistException {
        try {
            template.update("INSERT INTO users(login, password, email) VALUES(?, ?, ?)", login, password, email);
        } catch (DuplicateKeyException e) {
            throw new Utility.UserAlreadyExistException(e);
        }
    }

    @Override
    public void changePassword(String login, String newPassword) {
        template.update("UPDATE users SET password=? WHERE login=?", newPassword, login);
    }

    @Override
    public void setDetails(String login, String email) {
        template.update("UPDATE users SET email=? WHERE login=?", email, login);
    }

    @Override
    public UserProfile getUser(String login) {
        UserProfile up;
        try {
            up = template.queryForObject("SELECT login, password, email FROM users WHERE login=?", USER_MAPPER, login);
        } catch (EmptyResultDataAccessException e) {
            up = null;
        }
        return up;
    }

    @Override
    public List<UserProfile> listUsers(boolean order, int offset) {
        final String query = String.format("SELECT login, password, email FROM users ORDER BY login %s LIMIT 10 OFFSET %d", order ? "asc" : "desc", offset);
        return template.query(query, USER_MAPPER);
    }
}
