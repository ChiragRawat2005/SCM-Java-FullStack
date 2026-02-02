package com.scm.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.scm.entites.Contact;
import com.scm.entites.ContactInfoResponse;
import com.scm.entites.User;
import com.scm.helpers.Helper;
import com.scm.services.ContactService;
import com.scm.services.ImageService;
import com.scm.services.UserService;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private ContactService contactService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private UserService userService;

    @GetMapping("/view_contacts/{contactId}")
    public Contact getContact(@PathVariable String contactId) {
        return contactService.getContactById(contactId);
    }

    @DeleteMapping("/delete_contacts/{contactId}")
    public void deleteContact(@PathVariable String contactId) {
        Contact contact = contactService.getContactById(contactId);

        // DELETE IMAGE USING PUBLIC ID (NOT URL)
        if (contact.getPicturePublicId() != null && !contact.getPicturePublicId().isBlank()) {
            imageService.deleteImage(contact.getPicturePublicId());
        }

        // DELETE CONTACT FROM DB
        contactService.deleteContact(contactId);
    }

    @GetMapping("/export/contact-info/{id}")
    @ResponseBody
    public ContactInfoResponse getContactInfoForExport(
            @PathVariable String id,
            Authentication authentication) {

        String email = Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(email);

        Contact contact = contactService.getContactById(id);

        // Security check
        if (contact == null || !contact.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access");
        }

        return ContactInfoResponse.builder()
                .name(contact.getName())
                .email(contact.getEmail())
                .phone(contact.getPhone())
                .address(contact.getAddress())
                .message(contact.getMessage())
                .favourite(contact.isFavourite())
                .websiteLink(contact.getWebsiteLink())
                .linkedInLink(contact.getLinkedInLink())
                .picture(contact.getPicture())
                .build();
    }

}