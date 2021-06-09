package com.tnt.onlineshop.web.servlets;

import com.tnt.onlineshop.entity.Product;
import com.tnt.onlineshop.entity.User;
import com.tnt.onlineshop.service.UserService;
import com.tnt.onlineshop.service.impl.DefaultUserService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

class SignUpServletTest {

    private final UserService mockedUserService = mock(DefaultUserService.class);

    private final SignUpServlet signUpServlet = new SignUpServlet();
    private final HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
    private final HttpServletResponse mockedResponse = mock(HttpServletResponse.class);
    private final User mockedUser = mock(User.class);

    @BeforeEach
    void beforeEach() {
        reset(mockedUserService);
        reset(mockedRequest);
        reset(mockedResponse);
        reset(mockedUser);
    }


    @Test
    void doPost() throws IOException {
        //prepare
        String newEmail = "newuser@email.com";
        String password = "1234";
        StringReader reader = new StringReader("{\"email\":\""+ newEmail + "\", \"password\":\"" + password + "\"}");
        when(mockedRequest.getReader()).thenReturn(new BufferedReader(reader));
        when(mockedUserService.add(eq(newEmail), eq(password))).thenReturn(true);
        //when
        signUpServlet.doPost(mockedRequest, mockedResponse);
        //then
        verify(mockedRequest).getReader();
        verify(mockedUserService).add(eq(newEmail), eq(password));
        verify(mockedResponse).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void doPostFailedToAdd() throws IOException {
        //prepare
        String existingEmail = "existinguser@email.com";
        String password = "1234";
        StringReader reader = new StringReader("{\"email\":\""+ existingEmail + "\", \"password\":\"" + password + "\"}");
        when(mockedRequest.getReader()).thenReturn(new BufferedReader(reader));
        when(mockedUserService.add(eq(existingEmail), eq(password))).thenReturn(false);
        //when
        signUpServlet.doPost(mockedRequest, mockedResponse);
        //then
        verify(mockedRequest).getReader();
        verify(mockedUserService).add(eq(existingEmail), eq(password));
        verify(mockedResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

}