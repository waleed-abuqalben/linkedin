package com.linkedin.linkedin.features.authentication.controller;

import java.io.UnsupportedEncodingException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
