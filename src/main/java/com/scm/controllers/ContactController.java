package com.scm.controllers;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@SessionAttributes("loggedInUser")
public class ContactController {

    @Autowired
    public UserService userService;

    @Autowired
    private ContactService contactService;

    @Autowired
    private ImageService imageService;

    @GetMapping("/usercontact")
    public String showContactPage(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "6") int size,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            Model model, Authentication authentication) {

        String username = Helper.getEmailOfLoggedInUser(authentication);

        User user = userService.getUserByEmail(username);

        Page<Contact> contacts = contactService.getByUser(user, page, size, sortBy, direction);

        model.addAttribute("loggedInUser", user);
        model.addAttribute("contacts", contacts);

        return "user/contact";
    }

    @GetMapping("/search")
    public String searchHandler(
            @RequestParam(value = "field", defaultValue = "name") String field,
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "6") int size,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            Model model, Authentication authentication) {
        String email = Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(email);

        Page<Contact> search_result = this.contactService.searchContacts(user, field, keyword, page, size, sortBy,
                keyword);

        System.out.println("search result" + search_result);

        model.addAttribute("contacts", search_result);
        model.addAttribute("field", field);
        model.addAttribute("keyword", keyword);
        model.addAttribute("loggedInUser", user);

        return "user/search";
    }

    @GetMapping("/view_contacts/{contact_id}")
    public String viewPage(@PathVariable("contact_id") String contact_id, Model model, Authentication authentication) {

        String email = Helper.getEmailOfLoggedInUser(authentication);
        User loggedInUser = userService.getUserByEmail(email);

        var old_contact = this.contactService.getContactById(contact_id);
        ContactForm update_contact = new ContactForm();
        update_contact.setName(old_contact.getName());
        update_contact.setEmail(old_contact.getEmail());
        update_contact.setPhone(old_contact.getPhone());
        update_contact.setAddress(old_contact.getAddress());
        update_contact.setMessage(old_contact.getMessage());
        update_contact.setFavourite(old_contact.isFavourite());
        update_contact.setWebsiteLink(old_contact.getWebsiteLink());
        update_contact.setLinkedInLink(old_contact.getLinkedInLink());
        update_contact.setPicture_url(old_contact.getPicture());

        model.addAttribute("loggedInUser", loggedInUser);
        model.addAttribute("update_contact", update_contact);
        model.addAttribute("contact_id", contact_id);

        return "user/updateContact";
    }

    @PostMapping("/do-update/{id}")
    public String updateContact(
            @PathVariable("id") String id,
            @Valid @ModelAttribute("update_contact") ContactForm contactForm,
            BindingResult result,
            Authentication authentication,
            HttpSession session,
            Model model) throws IOException {

        // Fetch existing contact
        Contact existingContact = contactService.getContactById(id);

        if (existingContact == null) {
            session.setAttribute("message",
                    Message.builder()
                            .content("Contact not found")
                            .type(MessageType.red)
                            .build());
            return "redirect:/usercontact";
        }

        // authentication
        String email = Helper.getEmailOfLoggedInUser(authentication);
        User loggedInUser = userService.getUserByEmail(email);

        if (!existingContact.getUser().getId().equals(loggedInUser.getId())) {
            session.setAttribute("message",
                    Message.builder()
                            .content("You are not allowed to update this contact")
                            .type(MessageType.red)
                            .build());
            return "redirect:/usercontact";
        }

        // Image validation
        if (contactForm.getPicture() != null && !contactForm.getPicture().isEmpty()) {

            String contentType = contactForm.getPicture().getContentType();

            if (!(contentType.equals("image/jpeg")
                    || contentType.equals("image/png")
                    || contentType.equals("image/webp"))) {

                result.rejectValue(
                        "picture",
                        "error.picture",
                        "Only JPG, PNG and WEBP images are allowed");
            }
        }

        // Validation errors
        if (result.hasErrors()) {
            model.addAttribute("contact_id", id);
            model.addAttribute("loggedInUser", loggedInUser);

            session.setAttribute("message",
                    Message.builder()
                            .content("Please correct the highlighted errors")
                            .type(MessageType.red)
                            .build());

            return "user/updateContact";
        }

        // Update
        existingContact.setName(contactForm.getName());
        existingContact.setEmail(contactForm.getEmail());
        existingContact.setPhone(contactForm.getPhone());
        existingContact.setAddress(contactForm.getAddress());
        existingContact.setMessage(contactForm.getMessage());
        existingContact.setFavourite(contactForm.isFavourite());
        existingContact.setWebsiteLink(contactForm.getWebsiteLink());
        existingContact.setLinkedInLink(contactForm.getLinkedInLink());

        // Update image ONLY if new image uploaded
        if (contactForm.getPicture() != null && !contactForm.getPicture().isEmpty()) {

            // delete old image
            if (existingContact.getPicturePublicId() != null) {
                imageService.deleteImage(existingContact.getPicturePublicId());
            }

            String newPublicId = UUID.randomUUID().toString();
            String newImageUrl = imageService.uploadImage(
                    contactForm.getPicture(),
                    newPublicId);

            existingContact.setPicture(newImageUrl);
            existingContact.setPicturePublicId(newPublicId);
        }

        // Save updated contact
        contactService.saveContact(existingContact);

        // Success message
        session.setAttribute("message",
                Message.builder()
                        .content("Contact updated successfully")
                        .type(MessageType.green)
                        .build());

        // redirect to same page
        model.addAttribute("contact_id", id);
        return "user/updateContact";

        // redirect to contact
        // return "redirect:/usercontact";

    }

}