package com.linkedin.linkedin.features.authentication.controller;

import java.io.UnsupportedEncodingException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.linkedin.linkedin.features.authentication.dto.AuthenticationRequestBody;
import com.linkedin.linkedin.features.authentication.dto.AuthenticationResponseBody;
import com.linkedin.linkedin.features.authentication.model.AuthenticationUser;
import com.linkedin.linkedin.features.authentication.service.AuthenticationService;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/authentication")
public class AuthenticationController {
	
	private final AuthenticationService authenticationService;

	@GetMapping("/user")
	public AuthenticationUser getUser(
			@RequestAttribute("authenticatedUser") AuthenticationUser authenticationUser ) {//TODO: try-catch
		return authenticationService.getUser(authenticationUser.getEmail());
	}
	
	@PostMapping("/login")
	public AuthenticationResponseBody loginPage(
			@Valid @RequestBody AuthenticationRequestBody requestBody) {
		return authenticationService.login(requestBody);
	}
	
	@PostMapping("/register")
	public AuthenticationResponseBody registerPage(
			@Valid @RequestBody AuthenticationRequestBody requestBody) throws UnsupportedEncodingException, MessagingException {
		return authenticationService.register(requestBody);
	}
	
	@PutMapping("/validate-email-verification-token")
    public String verifyEmail(@RequestParam String token, @RequestAttribute("authenticatedUser") AuthenticationUser user) {
	 authenticationService.validateEmailVerificationToken(token, user.getEmail());
        return "Email verified successfully.";
    }

    @GetMapping("/send-email-verification-token")
    public String sendEmailVerificationToken(@RequestAttribute("authenticatedUser") AuthenticationUser user) {
    	authenticationService.sendEmailVerificationToken(user.getEmail());
        return "Email verification token sent successfully.";
    }

    @PutMapping("/send-password-reset-token")
    public String sendPasswordResetToken(@RequestParam String email) {
    	authenticationService.sendPasswordResetToken(email);
        return "Password reset token sent successfully.";
    }

    @PutMapping("/reset-password")
    public String resetPassword(@RequestParam String newPassword, @RequestParam String token, @RequestParam String email) {
    	authenticationService.resetPassword(email, newPassword, token);
        return "Password reset successfully.";
    }
    @PutMapping("/profile/{id}")
    public AuthenticationUser updateUserProfile(
            @RequestAttribute("authenticatedUser") AuthenticationUser user,
            @PathVariable Long id,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String location) {
    	
    	if (!user.getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have permission to update this profile.");
        }

        return authenticationService.updateUserProfile(id, firstName, lastName, company, position, location);
    	
    } 
    
    @DeleteMapping("/delete")
    public String deleteUser(@RequestAttribute("authenticatedUser") AuthenticationUser user) {
        authenticationService.deleteUser(user.getId());
        return "User deleted successfully.";
    }
    
    
    
    
    
}
