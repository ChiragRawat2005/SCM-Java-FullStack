package com.scm.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.scm.entites.User;
import com.scm.entites.UserUpdateForm;
import com.scm.helpers.Helper;
import com.scm.helpers.Message;
import com.scm.helpers.MessageType;
import com.scm.services.ImageService;
import com.scm.services.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

    @GetMapping("/profile")
    public String profilePage(Model model, Authentication authentication) {

        String email = Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(email);

        UserUpdateForm form = new UserUpdateForm();
        form.setName(user.getName());
        form.setPhone(user.getPhone());
        form.setMessage(user.getMessage());

        model.addAttribute("userForm", form);
        return "user/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(
            @Valid @ModelAttribute("userForm") UserUpdateForm form,
            BindingResult result,
            Authentication authentication,
            HttpSession session,
            Model model) {

        if (result.hasErrors()) {

            StringBuilder errorMsg = new StringBuilder();

            if (result.hasFieldErrors("phone")) {
                errorMsg.append("Invalid phone number.");
            }

            if (result.hasFieldErrors("newPassword")) {
                errorMsg.append("Password must contain at least 6 characters.");
            }

            if (result.hasFieldErrors("name")) {
                errorMsg.append("Invalid User Name! Must Between 3 - 50 Characters.");
            }

            // fallback (in case other validation errors occur)
            if (errorMsg.length() == 0) {
                errorMsg.append("Something went wrong. Please check your input.");
            }

            Message message = Message.builder()
                    .content(errorMsg.toString())
                    .type(MessageType.red)
                    .build();

            session.setAttribute("message", message);

            return "user/profile";
        }

        User user = userService.getUserByEmail(
                Helper.getEmailOfLoggedInUser(authentication));

        user.setName(form.getName());
        user.setPhone(form.getPhone());
        user.setMessage(form.getMessage());

        // password change
        if (form.getNewPassword() != null && !form.getNewPassword().isBlank()) {
            user.setPassword(form.getNewPassword()); // encoded in service
        }

        // profile image
        if (form.getProfilePic() != null && !form.getProfilePic().isEmpty()) {

            String publicId = "user_" + user.getId();
            String imageUrl = imageService.uploadImage(form.getProfilePic(), publicId);

            if (imageUrl != null) {
                user.setProfilePic(imageUrl);
                user.setProfilePicPublicId(publicId);
            }
        }

        userService.updateUser(user);

        session.setAttribute("message",
                Message.builder()
                        .content("Profile updated successfully")
                        .type(MessageType.green)
                        .build());

        return "redirect:/profile";
    }
}