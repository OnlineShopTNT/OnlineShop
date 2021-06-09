package com.tnt.onlineshop.web.servlets;

import com.tnt.onlineshop.entity.Session;
import com.tnt.onlineshop.entity.User;
import com.tnt.onlineshop.service.SessionService;
import com.tnt.onlineshop.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.io.*;
import java.util.Optional;

import static org.mockito.Mockito.*;

class SignInServletTest {

    private final UserService mockedUserService = mock(UserService.class);
    private final SessionService mockedSessionService = mock(SessionService.class);
    private final SignInServlet signInServlet = new SignInServlet(mockedUserService, mockedSessionService);

    private final HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
    private final HttpServletResponse mockedResponse = mock(HttpServletResponse.class);
    private final User mockedUser = mock(User.class);
    private final Session mockedSession = mock(Session.class);
    private final PrintWriter printWriter = new PrintWriter(new StringWriter());
    private final InOrder inOrderRequest = inOrder(mockedRequest);
    private final InOrder inOrderResponse = inOrder(mockedResponse);

    @BeforeEach
    void beforeEach() {
        reset(mockedRequest);
        reset(mockedResponse);
        reset(mockedUser);
        reset(mockedSession);
    }

    @Test
    @DisplayName("doPost when user doesn't exist")
    void doPostWhenUserDoesNotExist() throws IOException {
        //prepare
        String newEmail = "non-existent-user@onlineshop.com";
        String password = "password";
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        StringReader stringReader = new StringReader(getJsonString(newEmail, password));
        when(mockedRequest.getReader()).thenReturn(new BufferedReader(stringReader));
        when(mockedUserService.signInCheck(newEmail, password))
                .thenReturn(Optional.empty());
        //when
        signInServlet.doPost(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).getWriter();
        inOrderRequest.verify(mockedRequest).getReader();
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        inOrderResponse.verify(mockedResponse).setContentType("application/json");
        inOrderResponse.verify(mockedResponse).setCharacterEncoding("UTF-8");
    }

    @Test
    @DisplayName("doPost if session can't create")
    void doPostIfSessionCanNotCreate() throws IOException {
        //prepare
        String newEmail = "existing-user@onlineshop.com";
        String password = "password";
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        StringReader stringReader = new StringReader(getJsonString(newEmail, password));
        when(mockedRequest.getReader()).thenReturn(new BufferedReader(stringReader));
        when(mockedUserService.signInCheck(newEmail, password))
                .thenReturn(Optional.of(mockedUser));
        when(mockedSessionService.getByUser(mockedUser)).thenReturn(Optional.empty());
        //when
        signInServlet.doPost(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).getWriter();
        inOrderRequest.verify(mockedRequest).getReader();
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        inOrderResponse.verify(mockedResponse).setContentType("application/json");
        inOrderResponse.verify(mockedResponse).setCharacterEncoding("UTF-8");
    }

    @Test
    @DisplayName("doPost if session was created")
    void doPostIfSessionWasCreated() throws IOException {
        //prepare
        String newEmail = "existing-user@onlineshop.com";
        String password = "password";
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        StringReader stringReader = new StringReader(getJsonString(newEmail, password));
        when(mockedRequest.getReader()).thenReturn(new BufferedReader(stringReader));
        when(mockedUserService.signInCheck(newEmail, password))
                .thenReturn(Optional.of(mockedUser));
        when(mockedSessionService.getByUser(mockedUser)).thenReturn(Optional.of(mockedSession));
        when(mockedSession.getToken()).thenReturn("some-token");
        //when
        signInServlet.doPost(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).getWriter();
        inOrderRequest.verify(mockedRequest).getReader();
        inOrderResponse.verify(mockedResponse).addCookie(any());
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_OK);
        inOrderResponse.verify(mockedResponse).setContentType("application/json");
        inOrderResponse.verify(mockedResponse).setCharacterEncoding("UTF-8");
    }

    private String getJsonString(String email, String password) {
        return "{\"email\":\"" +
                email +
                "\", \"password\":\"" +
                password +
                "\"}";
    }

}