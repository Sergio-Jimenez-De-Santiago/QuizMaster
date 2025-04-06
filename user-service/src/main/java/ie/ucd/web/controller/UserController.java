package ie.ucd.web.controller;

import ie.ucd.web.model.User;
import ie.ucd.web.security.UserRole;
import ie.ucd.web.service.QuizService;
import ie.ucd.web.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private QuizService quizService;

    @GetMapping("/signup")
    public String showSignUpForm(User user, Model model, HttpSession session) {
        model.addAttribute("user", user);
        model.addAttribute("loggedInUser", session.getAttribute("loggedInUser"));

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser != null) {
            return "redirect:/index";
        }

        return "add-user";
    }

    @PostMapping("/adduser")
    public String addUser(@Valid User user, BindingResult result, Model model, HttpSession session) {
        if (result.hasErrors()) {
            return "add-user";
        }

        try {
            User newUser = userService.createUser(user);
            // Set the newUser as the loggedInUser
            session.setAttribute("loggedInUser", newUser);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Email already exists");
            return "add-user";
        }

        return "redirect:/index";
    }

    @GetMapping({"/index", "/"})
    public String showUserList(Model model, HttpSession session) {
        model.addAttribute("users", userService.getAllUsers());
        if(quizService==null){
            return "redirect:/<h1>hello</h1>";
        }
        model.addAttribute("quizzes", quizService.getAllQuizzes());
        model.addAttribute("loggedInUser", session.getAttribute("loggedInUser"));

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser != null) {
            model.addAttribute("admin", loggedInUser.getRole() == UserRole.ADMIN);
        }
        return "index";
    }

    @GetMapping({"/history"})
    public String showHistoryPage(HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/index";
        }

        return "history";
    }
}
