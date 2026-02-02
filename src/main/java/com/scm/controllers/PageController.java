package com.scm.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/about")
    public String aboutPage(Model model) {
        model.addAttribute("showFooter", true);
        return "about";
    }

    @GetMapping("/service")
    public String servicePage(Model model) {
        model.addAttribute("showFooter", true);
        return "service";
    }

    @GetMapping("/home")
    public String homePage(Model model) {
        model.addAttribute("showFooter", true);
        return "home";
    }

    @GetMapping("/")
    public String firstPage(Model model) {
        model.addAttribute("showFooter", true);
        return "home";
    }

    @GetMapping("/contactus")
    public String contactPage(Model model) {
        model.addAttribute("showFooter", true);
        return "contactus";
    }

    @GetMapping("/signin")
    public String signinPage() {
        return "signin";
    }

    @GetMapping("/privacy")
    public String privacy() {
        return "privacy";
    }

    @GetMapping("/terms")
    public String terms() {
        return "terms";
    }
}