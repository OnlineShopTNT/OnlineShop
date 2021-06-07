package com.tnt.onlineshop.web.filter;

import com.tnt.onlineshop.entity.Session;
import com.tnt.onlineshop.service.SessionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.io.IOException;
import java.util.Optional;

import static org.mockito.Mockito.*;

class SecurityFilterTest {

    private final HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
    private final HttpServletResponse mockedResponse = mock(HttpServletResponse.class);
    private final FilterChain mockedFilterChain = mock(FilterChain.class);
    private final SessionService mockedSessionService = mock(SessionService.class);
    private final SecurityFilter securityFilter = new SecurityFilter(mockedSessionService);
    private final InOrder inOrderRequest = inOrder(mockedRequest);

    @BeforeEach
    void beforeEach() {
        reset(mockedRequest);
        reset(mockedResponse);
        reset(mockedSessionService);
        reset(mockedFilterChain);
    }

    @Test
    void doFilterRequestWithoutSessionFilter() throws ServletException, IOException {
        //prepare
        when(mockedRequest.getMethod()).thenReturn("GET");
        when(mockedRequest.getRequestURI()).thenReturn("/products");
        //when
        securityFilter.doFilter(mockedRequest, mockedResponse, mockedFilterChain);
        //then
        inOrderRequest.verify(mockedRequest).getMethod();
        inOrderRequest.verify(mockedRequest).getRequestURI();
    }

    @Test
    void doFilterNoToken() throws ServletException, IOException {
        //prepare
        when(mockedRequest.getMethod()).thenReturn("POST");
        when(mockedRequest.getRequestURI()).thenReturn("/products");
        when(mockedRequest.getCookies()).thenReturn(new Cookie[]{});
        //when
        securityFilter.doFilter(mockedRequest, mockedResponse, mockedFilterChain);
        //then
        inOrderRequest.verify(mockedRequest).getMethod();
        inOrderRequest.verify(mockedRequest).getRequestURI();
        inOrderRequest.verify(mockedRequest).getCookies();
        verify(mockedResponse).setStatus(HttpServletResponse.SC_FORBIDDEN);
    }

    @Test
    void doFilterNonExistentToken() throws ServletException, IOException {
        //prepare
        when(mockedRequest.getMethod()).thenReturn("POST");
        when(mockedRequest.getRequestURI()).thenReturn("/products");
        Cookie cookieNonExistentToken = new Cookie("user-token", "new token");
        when(mockedRequest.getCookies()).thenReturn(new Cookie[]{cookieNonExistentToken});
        when(mockedSessionService.getByToken(cookieNonExistentToken.getValue())).thenReturn(Optional.empty());
        //when
        securityFilter.doFilter(mockedRequest, mockedResponse, mockedFilterChain);
        //then
        inOrderRequest.verify(mockedRequest).getMethod();
        inOrderRequest.verify(mockedRequest).getRequestURI();
        inOrderRequest.verify(mockedRequest).getCookies();
        verify(mockedSessionService).getByToken(cookieNonExistentToken.getValue());
        verify(mockedResponse).setStatus(HttpServletResponse.SC_FORBIDDEN);
    }

    @Test
    void doFilterExistingToken() throws ServletException, IOException {
        //prepare
        when(mockedRequest.getMethod()).thenReturn("POST");
        when(mockedRequest.getRequestURI()).thenReturn("/products");
        Cookie cookieExistingToken = new Cookie("user-token", "new token");
        when(mockedRequest.getCookies()).thenReturn(new Cookie[]{cookieExistingToken});
        when(mockedSessionService.getByToken(cookieExistingToken.getValue())).thenReturn(Optional.of(new Session()));
        //when
        securityFilter.doFilter(mockedRequest, mockedResponse, mockedFilterChain);
        //then
        inOrderRequest.verify(mockedRequest).getMethod();
        inOrderRequest.verify(mockedRequest).getRequestURI();
        inOrderRequest.verify(mockedRequest).getCookies();
        verify(mockedSessionService).getByToken(cookieExistingToken.getValue());
        verify(mockedFilterChain).doFilter(mockedRequest, mockedResponse);
    }

}