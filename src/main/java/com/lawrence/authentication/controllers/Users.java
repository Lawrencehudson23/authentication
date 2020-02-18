package com.lawrence.authentication.controllers;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lawrence.authentication.models.User;
import com.lawrence.authentication.services.UserService;

//imports removed for brevity
@Controller
public class Users {
	private final UserService userService;
	
	public Users(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/registration")
	public String registerForm(@ModelAttribute("user") User user) {
		return "registrationPage.jsp";
	}
	@GetMapping("/login")
	public String login() {
		return "loginPage.jsp";
	}

	@PostMapping("/registration")
	public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, HttpSession session) {
		
	// if result has errors, return the registration page (don't worry about validations just now)
     // else, save the user in the database, save the user id in session, and redirect them to the /home route
		
		if(result.hasErrors()) {
			return "registrationPage.jsp";
		} else {
			this.userService.registerUser(user);
			Long id = user.getId();
			session.setAttribute("id", id);
			return "redirect:/home";
		}
	
	}

	@PostMapping("/login")
	public String loginUser(@RequestParam("email") String email, @RequestParam("password") String password, Model model,
			HttpSession session,RedirectAttributes redirectAttributes) {
		// if the user is authenticated, save their user id in session
		// else, add error messages and return the login page
		if(this.userService.authenticateUser(email, password)) {
			User user=this.userService.findByEmail(email);
			session.setAttribute("id", user.getId());
			return "redirect:/home";
		}else {
			redirectAttributes.addFlashAttribute("error", "Cant login");
			return "redirect:/login";
		}
		 
	}

	@GetMapping("/home")
	public String home(HttpSession session, Model model) {
		// get user from session, save them in the model and return the home page
		Long id = (Long) session.getAttribute("id");
		User user = this.userService.findUserById(id);
		model.addAttribute("user", user);
		return "homePage.jsp";
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		// invalidate session
		// redirect to login page
		session.removeAttribute("id");
		return "redirect:/login";
		
	}
}