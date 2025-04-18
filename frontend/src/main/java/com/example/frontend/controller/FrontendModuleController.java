package com.example.frontend.controller;

import jakarta.servlet.http.HttpSession;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@Controller
public class FrontendModuleController {

    @Value("${module.service.url}")
    private String moduleServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    
    @GetMapping("/modules")
    public String viewModules(Model model, HttpSession session) {
        Object loggedInUser = session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        try {
            ResponseEntity<Module[]> response = restTemplate.getForEntity(
                    moduleServiceUrl + "/api/modules", Module[].class);
            List<Module> modules = Arrays.asList(response.getBody());
            model.addAttribute("modules", modules);
        } catch (Exception e) {
            model.addAttribute("error", "Unable to fetch modules.");
        }

        return "all-modules";
    }
}