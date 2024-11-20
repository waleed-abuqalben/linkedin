package com.linkedin.linkedin.features.authentication.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.linkedin.linkedin.features.feed.model.Post;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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

	private String firstName = null;
	private String lastName = null;
	private String company = null;
	private String position = null;
	private String location = null;
	private String profilePicture = null;
	private boolean profileComplete = false;
	
	//CascadeType.ALL -> if user deleted, all his posts deleted
	@OneToMany(
	 mappedBy ="author", 
	 cascade = CascadeType.ALL, orphanRemoval = true
	) 
	@JsonIgnore
	private List<Post> posts;

	public AuthenticationUser(String email, String password) {
		super();
		this.email = email;
		this.password = password;
	}

	private void updateProfileCompletionStatus() {
		this.profileComplete = (this.firstName != null && this.lastName != null && this.company != null
				&& this.position != null && this.location != null);
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
		updateProfileCompletionStatus();
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
		updateProfileCompletionStatus();
	}

	public void setLocation(String location) {
		this.location = location;
		updateProfileCompletionStatus();
	}

	public void setPosition(String position) {
		this.position = position;
		updateProfileCompletionStatus();
	}

	public void setCompany(String company) {
		this.company = company;
		updateProfileCompletionStatus();
	}
	
	

}
