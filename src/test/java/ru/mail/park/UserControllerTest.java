package ru.mail.park;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.mail.park.utility.Utility;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.jdbc.JdbcTestUtils.countRowsInTable;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
@Transactional
@ActiveProfiles("test")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate template;

    @Test
    public void testCreate() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/")
                .header("content-type", "application/json")
                .content(Utility.object2JSON(new Utility.RegistrationRequest("login", "password", "email"))))
                .andExpect(status().isOk());
        assertEquals(1, countRowsInTable(template, "user"));
    }

    @Test
    public void testGet() throws Exception {
        testCreate();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/login")
                .sessionAttr("login", "login"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.login").value("login"))
                .andExpect(jsonPath("$.email").value("email"));
    }

    @Test
    public void testListAll() throws Exception {
        testCreate();
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
        testCreate();
        mockMvc.perform(MockMvcRequestBuilders.put("/api/user/")
                .sessionAttr("login", "login")
                .header("content-type", "application/json")
                .content(Utility.object2JSON(new Utility.ChangeUserRequest("new_email"))))
                .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/login")
                .sessionAttr("login", "login"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.login").value("login"))
                .andExpect(jsonPath("$.email").value("new_email"));
    }
}
