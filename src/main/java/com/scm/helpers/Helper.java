package com.scm.helpers;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class Helper {

    public static String getEmailOfLoggedInUser(Authentication authentication) {

        if (authentication instanceof OAuth2AuthenticationToken) {

            // Give Provider Name(Google,Github,Facebook)
            var oauth2AuthenicationToken = (OAuth2AuthenticationToken) authentication;
            String authorizedClientRegistrationId = oauth2AuthenicationToken.getAuthorizedClientRegistrationId();

            // Give All The Attributes Of The Provider(name,email,profile,email_Verified)
            var oauthUser = (OAuth2User) authentication.getPrincipal();

            if (authorizedClientRegistrationId.equalsIgnoreCase("google")) {
                String google_email = oauthUser.getAttribute("email").toString();
                return google_email;
            }

            else if (authorizedClientRegistrationId.equalsIgnoreCase("github")) {
                String github_email = oauthUser.getAttribute("email") != null
                        ? oauthUser.getAttribute("email").toString()
                        : oauthUser.getAttribute("login").toString() + "@gmail.com";
                return github_email;
            }

            else if (authorizedClientRegistrationId.equalsIgnoreCase("facebook")) {
                String facebook_email = oauthUser.getAttribute("email").toString();
                return facebook_email;
            }

            else {
                throw new IllegalArgumentException("Unsupported OAuth2 provider: ");
            }
        }

        else {

            return authentication.getName();

        }
    }

    public static String getLinkForEmailVerification(String emailToken) {
        String link = "http://localhost:8080/auth/verify-email?token=" + emailToken;
        return link;
    }
}