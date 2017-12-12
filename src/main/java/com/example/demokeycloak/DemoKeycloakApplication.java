package com.example.demokeycloak;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.IDToken;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class DemoKeycloakApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoKeycloakApplication.class, args);
	}
}

@Controller
class ProductController {

	@GetMapping(path = "/products")
	public String getProducts(Model model, Principal principal){

		List<String> products = Arrays.asList("iPad","iPhone","iPod");
		if(((KeycloakPrincipal) principal).getKeycloakSecurityContext()
				.getToken()
				.getRealmAccess()
				.getRoles()
				.contains("admin")) {
			// Admins will see different products
			products = Arrays.asList("MacBook Pro","iMac 5K","Mac mini");
		}
		model.addAttribute("products", products);
		model.addAttribute("principal", principal);
		model.addAttribute("accesstoken", ((KeycloakPrincipal) principal).getKeycloakSecurityContext().getToken());
		return "products";
	}

	@GetMapping(path = "/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response) throws ServletException {

		// Invalidate Keycloak Session
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.setAttribute(KeycloakSecurityContext.class.getName(), null);
			session.invalidate();
		}
		request.logout();

		// Disable Caching of logout call
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Expires", "0");

		return "/";
	}
}
