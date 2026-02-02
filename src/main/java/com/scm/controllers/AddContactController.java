package com.scm.controllers;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.scm.entites.Contact;
import com.scm.entites.ContactForm;
import com.scm.entites.User;
import com.scm.helpers.Helper;
import com.scm.helpers.Message;
import com.scm.helpers.MessageType;
import com.scm.services.ContactService;
import com.scm.services.ImageService;
import com.scm.services.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class AddContactController {

    @Autowired
    public UserService userService;

    @Autowired
    private ContactService contactService;

    @Autowired
    private ImageService imageService;

    @GetMapping("/addcontact")
    public String addcontactPage(Model model, Authentication authentication) {
        String email = Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(email);

        model.addAttribute("loggedInUser", user);
        model.addAttribute("contactForm", new ContactForm());
        return "user/addcontact";
    }

    @PostMapping("/do-add")
    public String saveContact(@Valid @ModelAttribute("contactForm") ContactForm contactForm, BindingResult result,
            Authentication authentication, HttpSession session, Model model) throws IOException {

        // Image Type Validation
        if (contactForm.getPicture() != null && !contactForm.getPicture().isEmpty()) {

            String contentType = contactForm.getPicture().getContentType();

            if (!(contentType.equals("image/jpeg")
                    || contentType.equals("image/png")
                    || contentType.equals("image/webp"))) {

                // Attach error to picture field
                result.rejectValue(
                        "picture",
                        "error.picture",
                        "Only JPG, PNG and WEBP images are allowed");
            }
        }

        if (result.hasErrors()) {
            // Message
            String email = Helper.getEmailOfLoggedInUser(authentication);
            User user = userService.getUserByEmail(email);

            model.addAttribute("loggedInUser", user); // ✅ IMPORTANT
            model.addAttribute("contactForm", contactForm);

            Message message = Message.builder()
                    .content("Please Correct The Following Error's")
                    .type(MessageType.red)
                    .build();

            session.setAttribute("message", message);

            return "user/addcontact";
        }

        String username = Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(username);

        Contact contact = new Contact();
        contact.setName(contactForm.getName());
        contact.setFavourite(contactForm.isFavourite());
        contact.setEmail(contactForm.getEmail());
        contact.setPhone(contactForm.getPhone());
        contact.setAddress(contactForm.getAddress());
        contact.setMessage(contactForm.getMessage());
        contact.setUser(user);
        contact.setLinkedInLink(contactForm.getLinkedInLink());
        contact.setWebsiteLink(contactForm.getWebsiteLink());

        // Image To Cloud
        if (contactForm.getPicture() != null && !contactForm.getPicture().isEmpty()) {
            String filename = UUID.randomUUID().toString();
            String image_url = imageService.uploadImage(contactForm.getPicture(), filename);
            contact.setPicture(image_url);
            contact.setPicturePublicId(filename);
        }

        // Image To DB
        // if (!contactForm.getPicture().isEmpty()) {
        // contact.setPicture(contactForm.getPicture().getBytes());
        // contact.setPicture_type(contactForm.getPicture().getContentType());
        // }

        // Save To DB
        contactService.saveContact(contact);

        // Message
        Message message = Message.builder()
                .content("New Contact Added Successfully")
                .type(MessageType.green)
                .build();

        session.setAttribute("message", message);

        return "redirect:/addcontact";
    }
}