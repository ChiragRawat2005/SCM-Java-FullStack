package com.scm.config;

import java.io.IOException;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.scm.helpers.Message;
import com.scm.helpers.MessageType;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class AuthFailureHandler implements AuthenticationFailureHandler {

    // If User Is Disabled
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {

        HttpSession session = request.getSession();

        if (exception instanceof DisabledException) {

            session.setAttribute(
                    "message",
                    Message.builder()
                            .content(
                                    "Your Account Is Currently Inactive. Verification Link Has Been Sent To Your Registered Email.")
                            .type(MessageType.red)
                            .build());

        } else {

            session.setAttribute(
                    "message",
                    Message.builder()
                            .content("Invalid Email or Password")
                            .type(MessageType.red)
                            .build());
        }

        response.sendRedirect("/signin");
    }
}