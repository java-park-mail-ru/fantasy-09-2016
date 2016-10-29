package ru.mail.park;

import org.junit.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.mail.park.utility.Utility;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class SessionControllerTest extends AbstractTest {

    @Test
    public void testLogin() throws Exception {
        testCreateUser();
        mockMvc.perform(MockMvcRequestBuilders.post("/api/session/")
                .header("content-type", "application/json")
                .content(Utility.object2JSON(new Utility.LoginRequest("", ""))))
                .andExpect(status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.post("/api/session/")
                .header("content-type", "application/json")
                .content(Utility.object2JSON(new Utility.LoginRequest("login", "password"))))
                .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.post("/api/session/")
                .header("content-type", "application/json")
                .content(Utility.object2JSON(new Utility.LoginRequest("login", "wrong_password"))))
                .andExpect(status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.post("/api/session/")
                .header("content-type", "application/json")
                .content(Utility.object2JSON(new Utility.LoginRequest("login", "password")))
                .sessionAttr("login", "login"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testLogout() throws Exception {
        testCreateUser();
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/session/")
                .header("content-type", "application/json"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/session/")
                .header("content-type", "application/json")
                .sessionAttr("login", "login"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetUser() throws Exception {
        testCreateUser();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/session/")
                .sessionAttr("login", "login"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.login").value("login"))
                .andExpect(jsonPath("$.email").value("email"));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/session/")
                .header("content-type", "application/json"))
                .andExpect(status().isBadRequest());
    }
}
