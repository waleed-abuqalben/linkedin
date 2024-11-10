package com.linkedin.linkedin.features.authentication.model;


import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "users")
public class AuthenticationUser {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(unique = true)
	@NotNull
	@Email
	private String email;
	private Boolean emailVerified = false;
	private String emailVerificationToken = null;
	private LocalDateTime emailVerificationTokenExpiryDate = null;
	
	@JsonIgnore
	private String password;
	private String passwordResetToken = null;
	private LocalDateTime passwordResetTokenExpiryDate = null;
	
	public AuthenticationUser(String email, String password) {
		super();
		this.email = email;
		this.password = password;
	}
	
	

}
