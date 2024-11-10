package com.linkedin.linkedin.features.authentication.service;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.linkedin.linkedin.features.authentication.dto.AuthenticationRequestBody;
import com.linkedin.linkedin.features.authentication.dto.AuthenticationResponseBody;
import com.linkedin.linkedin.features.authentication.model.AuthenticationUser;
import com.linkedin.linkedin.features.authentication.repository.AuthenticationUserRepository;
import com.linkedin.linkedin.features.authentication.utils.EmailService;
import com.linkedin.linkedin.features.authentication.utils.Encoder;
import com.linkedin.linkedin.features.authentication.utils.JsonWebToken;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AuthenticationService {
	private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
	private final int durationInMinutes = 3;
	private final AuthenticationUserRepository authUserRepo;
	private final Encoder encoder;
	private final JsonWebToken jsonWebToken;
	private final EmailService emailService;

	
	
	public AuthenticationUser getUser(String email) {
		return authUserRepo.findByEmail(email)
			.orElseThrow(() -> //TODO: RecourseNotFound
			new IllegalArgumentException("user not found"));
	}
	
	public AuthenticationResponseBody login(AuthenticationRequestBody loginRequestBody) {
		AuthenticationUser user = //TODO: getUser(requestBody.getEmail());
			authUserRepo.findByEmail(loginRequestBody.getEmail())
				.orElseThrow(() -> //TODO: RecourseNotFound
				new IllegalArgumentException("user not found"));
		if(!encoder.matches(loginRequestBody.getPassowrd(), user.getPassword())) {
			throw new IllegalArgumentException("Password is incorrect");
		}
		String token = jsonWebToken.generateToken(loginRequestBody.getEmail());
		return new AuthenticationResponseBody(token, "Authentication success");
		
	}
	
	//generate 5 digit otp
	public static String generateEmailVerificationToken() {
        SecureRandom random = new SecureRandom();
        StringBuilder token = new StringBuilder(5);
        for (int i = 0; i < 5; i++) {
            token.append(random.nextInt(10)); // Appending random digit from 0 to 9
        }
        return token.toString();
    }
	
	public void sendEmailVerificationToken(String email) {
        Optional<AuthenticationUser> user = authUserRepo.findByEmail(email);
        if (user.isPresent() && !user.get().getEmailVerified()) {
            String emailVerificationToken = generateEmailVerificationToken();
            String hashedToken = encoder.encode(emailVerificationToken);
            user.get().setEmailVerificationToken(hashedToken);
            user.get().setEmailVerificationTokenExpiryDate(LocalDateTime.now().plusMinutes(durationInMinutes));
            authUserRepo.save(user.get());
            String subject = "Email Verification";
            String body = String.format("Only one step to take full advantage of LinkedIn.\n\n"
                            + "Enter this code to verify your email: " + "%s\n\n" + "The code will expire in " + "%s" + " minutes.",
                    emailVerificationToken, durationInMinutes);
            try {
                emailService.sendEmail(email, subject, body);
            } catch (Exception e) {
                logger.info("Error while sending email: {}", e.getMessage());
            }
        } else {
            throw new IllegalArgumentException("Email verification token failed, or email is already verified.");
        }
    }

	 public void validateEmailVerificationToken(String token, String email) {
		    logger.info("token: {}, email: {}", token, email);
	        Optional<AuthenticationUser> user = authUserRepo.findByEmail(email);
	        if (user.isPresent() 
	        		&& encoder.matches(token, user.get().getEmailVerificationToken()) 
	        		&& !user.get().getEmailVerificationTokenExpiryDate().isBefore(LocalDateTime.now())) {
	            
	        	user.get().setEmailVerified(true);
	            user.get().setEmailVerificationToken(null);
	            user.get().setEmailVerificationTokenExpiryDate(null);
	            authUserRepo.save(user.get());
	        } else if (user.isPresent() && encoder.matches(token, user.get().getEmailVerificationToken()) && user.get().getEmailVerificationTokenExpiryDate().isBefore(LocalDateTime.now())) {
	            throw new IllegalArgumentException("Email verification token expired.");
	        } else {
	            throw new IllegalArgumentException("Email verification token failed.");
	        }
	    }
	
	public AuthenticationResponseBody register(AuthenticationRequestBody registerRequestBody) {
        AuthenticationUser user = authUserRepo.save(new AuthenticationUser(registerRequestBody.getEmail(), encoder.encode(registerRequestBody.getPassowrd())));

        String emailVerificationToken = generateEmailVerificationToken();
        String hashedToken = encoder.encode(emailVerificationToken);
        user.setEmailVerificationToken(hashedToken);
        user.setEmailVerificationTokenExpiryDate(LocalDateTime.now().plusMinutes(durationInMinutes));

        authUserRepo.save(user);

        String subject = "Email Verification";
        String body = String.format("""
                        Only one step to take full advantage of LinkedIn.
                        
                        Enter this code to verify your email: %s. The code will expire in %s minutes.""",
                emailVerificationToken, durationInMinutes); // Include the token in the message body
        try {
            emailService.sendEmail(registerRequestBody.getEmail(), subject, body);
        } catch (Exception e) {
            logger.info("Error while sending email: {}", e.getMessage());
        }
        String authToken = jsonWebToken.generateToken(registerRequestBody.getEmail());
        return new AuthenticationResponseBody(authToken, "User registered successfully.");
    }
	
	// Password reset logic
    public void sendPasswordResetToken(String email) {
        Optional<AuthenticationUser> user = authUserRepo.findByEmail(email);
        if (user.isPresent()) {
            String passwordResetToken = generateEmailVerificationToken();
            String hashedToken = encoder.encode(passwordResetToken);
            user.get().setPasswordResetToken(hashedToken);
            user.get().setPasswordResetTokenExpiryDate(LocalDateTime.now().plusMinutes(durationInMinutes));
            authUserRepo.save(user.get());
            String subject = "Password Reset";
            String body = String.format("""
                            You requested a password reset.
                            
                            Enter this code to reset your password: %s. The code will expire in %s minutes.""",
                    passwordResetToken, durationInMinutes);
            try {
                emailService.sendEmail(email, subject, body);
            } catch (Exception e) {
                logger.info("Error while sending email: {}", e.getMessage());
            }
        } else {
            throw new IllegalArgumentException("User not found.");
        }
    }

    public void resetPassword(String email, String newPassword, String token) {
        Optional<AuthenticationUser> user = authUserRepo.findByEmail(email);
        if (user.isPresent() && encoder.matches(token, user.get().getPasswordResetToken()) && !user.get().getPasswordResetTokenExpiryDate().isBefore(LocalDateTime.now())) {
            user.get().setPasswordResetToken(null);
            user.get().setPasswordResetTokenExpiryDate(null);
            user.get().setPassword(encoder.encode(newPassword));
            authUserRepo.save(user.get());
        } else if (user.isPresent() && encoder.matches(token, user.get().getPasswordResetToken()) && user.get().getPasswordResetTokenExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Password reset token expired.");
        } else {
            throw new IllegalArgumentException("Password reset token failed.");
        }
    }
	

	
	
}