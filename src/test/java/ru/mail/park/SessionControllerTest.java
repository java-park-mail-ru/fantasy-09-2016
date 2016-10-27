package ru.mail.park;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.mail.park.utility.Utility;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
@Transactional
@ActiveProfiles("test")
public class SessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private void createUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/")
                .header("content-type", "application/json")
                .content(Utility.object2JSON(new Utility.RegistrationRequest("login", "password", "email"))))
                .andExpect(status().isOk());
    }

    @Test
    public void testLogin() throws Exception {
        createUser();
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
        createUser();
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/session/")
                .header("content-type", "application/json"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/session/")
                .header("content-type", "application/json")
                .sessionAttr("login", "login"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGet() throws Exception {
        createUser();
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
