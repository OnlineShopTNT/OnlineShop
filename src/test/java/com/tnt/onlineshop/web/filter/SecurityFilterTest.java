package com.tnt.onlineshop.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecurityFilterTest {

    private final HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
    private final HttpServletResponse mockedResponse = mock(HttpServletResponse.class);
    private final FilterChain mockedFilterChain = mock(FilterChain.class);
    private final SecurityFilter securityFilter = new SecurityFilter();
    private final InOrder inOrderRequest = inOrder(mockedRequest);
    private final InOrder inOrderResponse = inOrder(mockedResponse);

    @BeforeEach
    void beforeEach() {
        reset(mockedRequest);
        reset(mockedResponse);
    }

    @Test
    void doFilter() throws ServletException, IOException {
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

}