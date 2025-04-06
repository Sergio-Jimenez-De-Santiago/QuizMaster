package ie.ucd.web.controller;

import ie.ucd.web.model.User;
import ie.ucd.web.service.QuizService;
import ie.ucd.web.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {
    private final UserService userService;
    @Autowired
    private QuizService quizService;
    @Autowired
    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLoginForm(Model model, HttpSession session) {
        model.addAttribute("user", new User());
        model.addAttribute("loggedInUser", session.getAttribute("loggedInUser"));

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser != null) {
            return "redirect:/index";
        }

        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@Valid User user, BindingResult result, Model model, HttpSession session) {
        model.addAttribute("loggedInUser", session.getAttribute("loggedInUser"));

        if (result.hasErrors()) {
            return "login";
        }

        // Check if the user exists in the database
        User existingUser = userService.findByEmail(user.getEmail());
        if (existingUser == null || !existingUser.getPassword().equals(user.getPassword()) || !existingUser.getName().equals(user.getName())) {
            model.addAttribute("error", true);
            return "login";
        }

        // Store the logged-in user in the session
        session.setAttribute("loggedInUser", existingUser);

        return "redirect:/index";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/index";
        }

        session.invalidate();
        return "redirect:/index";
    }
}
