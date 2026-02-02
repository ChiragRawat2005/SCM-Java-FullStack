package com.scm.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.scm.entites.User;
import com.scm.helpers.Helper;
import com.scm.repository.UserRepository;

@ControllerAdvice
public class RootController {

    @Autowired
    private UserRepository repo;

    @ModelAttribute
    public void signInUserDetails(Model model, Authentication authentication) {

        if (authentication == null)
            return;

        String email = Helper.getEmailOfLoggedInUser(authentication);
        User dao_user = repo.findByEmail(email).orElse(null);

        System.out.println("Sign in user = " + dao_user);

        model.addAttribute("loggedInUser", dao_user);
    }
}
