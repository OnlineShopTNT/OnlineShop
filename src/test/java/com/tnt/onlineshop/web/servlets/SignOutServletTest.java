package com.tnt.onlineshop.web.servlets;

import com.tnt.onlineshop.entity.Session;
import com.tnt.onlineshop.entity.User;
import com.tnt.onlineshop.service.SessionService;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class SignOutServletTest {

    private final SessionService mockedSessionService = mock(SessionService.class);
    private final SignOutServlet signOutServlet = new SignOutServlet(mockedSessionService);
    private final HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
    private final HttpServletResponse mockedResponse = mock(HttpServletResponse.class);
    private final User mockedUser = mock(User.class);
    private final Session mockedSession = mock(Session.class);

    @BeforeEach
    void beforeEach() {
        reset(mockedRequest);
        reset(mockedResponse);
        reset(mockedUser);
        reset(mockedSession);
    }

    @Test
    void doPost() {
        //prepare
        String token = "token";
        when(mockedRequest.getCookies()).thenReturn(new Cookie[]{new Cookie("user-token", token)});
        when(mockedSessionService.delete(token)).thenReturn(true);
        //when
        signOutServlet.doPost(mockedRequest, mockedResponse);
        //then
        verify(mockedRequest).getCookies();
        verify(mockedSessionService).delete(token);
        verify(mockedResponse).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void doPostNoCookie() {
        //prepare
        when(mockedRequest.getCookies()).thenReturn(null);
        //when
        signOutServlet.doPost(mockedRequest, mockedResponse);
        //then
        verify(mockedRequest).getCookies();
        verify(mockedSessionService, never()).delete(anyString());
        verify(mockedResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void doPostNoUserTokenCookie() {
        //prepare
        when(mockedRequest.getCookies()).thenReturn(new Cookie[]{new Cookie("product-id", "")});
        //when
        signOutServlet.doPost(mockedRequest, mockedResponse);
        //then
        verify(mockedRequest).getCookies();
        verify(mockedSessionService, never()).delete(anyString());
        verify(mockedResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void doPostFailToDeleteByToken() {
        //prepare
        String noSessionToken = "invalid token";
        when(mockedRequest.getCookies()).thenReturn(new Cookie[]{new Cookie("user-token", noSessionToken)});
        when(mockedSessionService.delete(noSessionToken)).thenReturn(false);
        //when
        signOutServlet.doPost(mockedRequest, mockedResponse);
        //then
        verify(mockedRequest).getCookies();
        verify(mockedSessionService).delete(noSessionToken);
        verify(mockedResponse).setStatus(HttpServletResponse.SC_FORBIDDEN);
    }

}