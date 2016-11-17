package ru.mail.park;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import ru.mail.park.model.UserProfile;
import ru.mail.park.services.IAccountService;
import ru.mail.park.utility.Utility;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(WebSocketConfig.class)
public class WebSocketTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate template;

    @MockBean
    private IAccountService accountService;

    @Test
    public void test() throws Exception {

        Mockito.when(accountService.getUser("foo")).thenReturn(new UserProfile("foo", "pass", ""));
        final ResponseEntity<String> loginResp = template.postForEntity("/api/session",
                new Utility.LoginRequest("foo", "pass"), String.class);

        assertEquals(HttpStatus.OK, loginResp.getStatusCode());
        final List<String> sessionCookie = loginResp.getHeaders().get(HttpHeaders.SET_COOKIE);

        final StandardWebSocketClient webSocketClient = new StandardWebSocketClient();

        final WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.put(HttpHeaders.COOKIE, sessionCookie);

        final TestWebSockethandler testWebSockethandler = new TestWebSockethandler();
        final ListenableFuture<WebSocketSession> future = webSocketClient.doHandshake(testWebSockethandler, headers,
                new URI("ws://localhost:" + port + "/game"));
        final WebSocketSession socketSession = future.get();
        assertNotNull(socketSession);
        socketSession.sendMessage(new TextMessage("me"));
        testWebSockethandler.messageRecieved.await();
        assertEquals("foo", testWebSockethandler.payload);

    }

    private static class TestWebSockethandler extends TextWebSocketHandler {
        private final CountDownLatch messageRecieved = new CountDownLatch(1);
        private String payload;

        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
            payload = message.getPayload();
            messageRecieved.countDown();
        }
    }

}
