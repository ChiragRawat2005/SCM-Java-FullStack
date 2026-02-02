package com.scm.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.scm.entites.User;
import com.scm.entites.UserForm;
import com.scm.helpers.Message;
import com.scm.helpers.MessageType;
import com.scm.services.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class SignUpController {

    @GetMapping(path = "/signup")
    public String signupPage(Model model) {
        model.addAttribute("user", new UserForm());
        return "signup";
    }

    @Autowired
    private UserService userService;

    @PostMapping(path = "/do-signup")
    public String signupSave(@Valid @ModelAttribute("user") UserForm userForm, BindingResult result,
            HttpSession session) {

        if (result.hasErrors()) {
            return "signup";
        }

        if (userService.emailExists(userForm.getEmail())) {
            Message message = Message.builder()
                    .content("Email already registered!")
                    .type(MessageType.red)
                    .build();

            session.setAttribute("message", message);
            return "redirect:/signup";
        }

        // Save In DB (UserForm -> user)
        User user = new User();
        user.setName(userForm.getName());
        user.setEmail(userForm.getEmail());
        user.setPassword(userForm.getPassword());
        user.setPhone(userForm.getPhone());
        user.setMessage(userForm.getMessage());

        userService.saveUser(user);

        // Message
        Message message = Message.builder()
                .content("Registration Successful. Verification Link Has Been Sent To Your Registered Email Please Verify Before Signin.")
                .type(MessageType.green)
                .build();

        session.setAttribute("message", message);

        return "redirect:/signup";
    }

}