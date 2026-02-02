package com.scm.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.scm.entites.Contact;
import com.scm.entites.User;
import com.scm.helpers.Helper;
import com.scm.services.ContactService;
import com.scm.services.UserService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class UserController {

    @Autowired
    public UserService userService;

    @Autowired
    private ContactService contactService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {

        String email = Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(email);

        long totalContacts = contactService.countContactsByUser(user);
        long favouriteContacts = contactService.countFavouriteContactsByUser(user);

        model.addAttribute("loggedInUser", user);
        model.addAttribute("totalContacts", totalContacts);
        model.addAttribute("favouriteContacts", favouriteContacts);

        return "user/dashboard";
    }

    @GetMapping("/exportcontacts")
    public String exportContactsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            Model model,
            Authentication authentication) {

        String email = Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(email);

        Page<Contact> contacts = contactService.getByUser(user, page, size, sortBy, direction);

        model.addAttribute("loggedInUser", user);
        model.addAttribute("contacts", contacts);

        return "user/exportcontact";
    }

    @GetMapping("/exportcontacts/search")
    public String exportContactsSearch(
            @RequestParam(value = "field", defaultValue = "name") String field,
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            Model model,
            Authentication authentication) {

        String email = Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(email);

        Page<Contact> contacts = contactService.searchContacts(user, field, keyword, page, size, "name", "asc");

        model.addAttribute("loggedInUser", user);
        model.addAttribute("contacts", contacts);
        model.addAttribute("field", field);
        model.addAttribute("keyword", keyword);

        return "user/exportcontact";
    }

    @GetMapping("/exportcontacts/download")
    public void exportAllContacts(HttpServletResponse response,
            Authentication authentication) throws IOException {

        String email = Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(email);

        List<Contact> contacts = contactService.getByUserId(user.getId());

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition",
                "attachment; filename=contacts.csv");

        PrintWriter writer = response.getWriter();

        // CSV Header
        writer.println("Name,Email,Phone,Address,Message,Favourite,Website,LinkedIn");

        for (Contact c : contacts) {
            writer.println(
                    safe(c.getName()) + "," +
                            safe(c.getEmail()) + "," +
                            safe(c.getPhone()) + "," +
                            safe(c.getAddress()) + "," +
                            safe(c.getMessage()) + "," +
                            (c.isFavourite() ? "TRUE" : "FALSE") + "," +
                            safe(c.getWebsiteLink()) + "," +
                            safe(c.getLinkedInLink()));
        }

        writer.flush();
        writer.close();
    }

    private String safe(String val) {
        return val == null ? "" : val.replace(",", " ");
    }

}