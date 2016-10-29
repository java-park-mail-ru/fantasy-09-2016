package ru.mail.park;

import org.junit.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.mail.park.utility.Utility;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTest extends AbstractTest {

    @Test
    public void testCreate() throws Exception {
        testCreateUser();
    }

    @Test
    public void testGet() throws Exception {
        testCreateUser();
        testGetUser("email");
    }

    @Test
    public void testListAll() throws Exception {
        testCreateUser();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/")
                .sessionAttr("login", "login"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.users").isArray())
                .andExpect(jsonPath("$.users", hasSize(1)))
                .andExpect(jsonPath("$.users[0]").isMap())
                .andExpect(jsonPath("$.users[0].login").value("login"))
                .andExpect(jsonPath("$.users[0].email").value("email"));
    }

    @Test
    public void testPut() throws Exception {
        testCreateUser();
        mockMvc.perform(MockMvcRequestBuilders.put("/api/user/")
                .sessionAttr("login", "login")
                .header("content-type", "application/json")
                .content(Utility.object2JSON(new Utility.ChangeUserRequest("new_email"))))
                .andExpect(status().isOk());
        testGetUser("new_email");
    }
}
