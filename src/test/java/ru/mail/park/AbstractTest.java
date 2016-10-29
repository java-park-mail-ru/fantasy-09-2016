package ru.mail.park;

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

import static org.junit.Assert.assertEquals;
import static org.springframework.test.jdbc.JdbcTestUtils.countRowsInTable;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
@Transactional
@ActiveProfiles("test")
public abstract class AbstractTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected JdbcTemplate template;

    public void testCreateUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/")
                .header("content-type", "application/json")
                .content(Utility.object2JSON(new Utility.RegistrationRequest("login", "password", "email"))))
                .andExpect(status().isOk());
        assertEquals(1, countRowsInTable(template, "user"));
    }

    public void testGetUser(String email) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/login")
                .sessionAttr("login", "login"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.login").value("login"))
                .andExpect(jsonPath("$.email").value(email));
    }
}
