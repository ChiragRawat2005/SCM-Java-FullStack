package com.scm.config;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.scm.entites.Providers;
import com.scm.entites.User;
import com.scm.repository.UserRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuthAuthenicationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserRepository repo;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        System.out.println("aouth2.0 Success");

        // Give Provider Name(Google,Github,Facebook)
        var oauth2AuthenicationToken = (OAuth2AuthenticationToken) authentication;
        String authorizedClientRegistrationId = oauth2AuthenicationToken.getAuthorizedClientRegistrationId();
        System.out.println(authorizedClientRegistrationId);

        // Give All The Attributes Of The Provider(name,email,profile,email_Verified)
        var oauthUser = (OAuth2User) authentication.getPrincipal();
        oauthUser.getAttributes().forEach((key, value) -> {
            System.out.println(key + " : " + value);
        });

        // Default Setting Feild
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setPassword("password");
        user.setEnabled(true);
        user.setEmailVerified(true);
        user.setRoleList(List.of("USER"));
        user.setPhoneVerified(false);

        if (authorizedClientRegistrationId.equalsIgnoreCase("google")) {
            user.setName(oauthUser.getAttribute("name").toString());
            user.setEmail(oauthUser.getAttribute("email").toString());
            user.setProfilePic(oauthUser.getAttribute("picture").toString());
            user.setProviderUserId(oauthUser.getName());
            user.setProvider(Providers.GOOGLE);
            user.setMessage("This Account Is Created Using Google");
        }

        else if (authorizedClientRegistrationId.equalsIgnoreCase("github")) {
            user.setName(oauthUser.getAttribute("login").toString());
            user.setEmail(oauthUser.getAttribute("email") != null ? oauthUser.getAttribute("email").toString()
                    : oauthUser.getAttribute("login").toString() + "@gmail.com");
            user.setProfilePic(oauthUser.getAttribute("avatar_url").toString());
            user.setProviderUserId(oauthUser.getName());
            user.setProvider(Providers.GITHUB);
            user.setMessage("This Account Is Created Using Github");
        }

        else if (authorizedClientRegistrationId.equalsIgnoreCase("facebook")) {
            String name = oauthUser.getAttribute("name");

            String email = oauthUser.getAttribute("email");
            if (email == null) {
                email = oauthUser.getName() + "@facebook.com"; // fallback
            }

            // Extract profile picture safely
            Map<String, Object> pictureObj = oauthUser.getAttribute("picture");
            String pictureUrl = null;

            if (pictureObj != null) {
                Map<String, Object> data = (Map<String, Object>) pictureObj.get("data");
                if (data != null) {
                    pictureUrl = (String) data.get("url");
                }
            }

            user.setName(name);
            user.setEmail(email);
            user.setProfilePic(pictureUrl);
            user.setProviderUserId(oauthUser.getName());
            user.setProvider(Providers.FACEBOOK);
            user.setMessage("This Account Is Created Using Facebook");
        }

        else {
            System.out.println("Unknown Provider");
        }

        User userDao = this.repo.findByEmail(user.getEmail()).orElse(null);

        if (userDao == null) {
            this.repo.saveAndFlush(user); // force DB write
        } else {
            user = userDao; // reuse existing user
        }

        new DefaultRedirectStrategy().sendRedirect(request, response, "/dashboard");
    }
}