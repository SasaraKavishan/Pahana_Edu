package com.pahanaedu.bookshop.servlet;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.mockito.Mockito.*;

class HelpServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private RequestDispatcher requestDispatcher;

    @InjectMocks
    private HelpServlet helpServlet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(request.getRequestDispatcher("help.jsp")).thenReturn(requestDispatcher);
    }

    @Test
    void testDoGetForwardsToHelpJsp() throws ServletException, IOException {

        helpServlet.doGet(request, response);


        verify(request).getRequestDispatcher("help.jsp");
        verify(requestDispatcher).forward(request, response);
    }
}