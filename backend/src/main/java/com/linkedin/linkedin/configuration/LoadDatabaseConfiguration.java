package com.linkedin.linkedin.configuration;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.linkedin.linkedin.features.authentication.model.AuthenticationUser;
import com.linkedin.linkedin.features.authentication.repository.AuthenticationUserRepository;
import com.linkedin.linkedin.features.authentication.utils.Encoder;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class LoadDatabaseConfiguration {

	private final Encoder encoder;
	@Bean
	public CommandLineRunner initDatabase(AuthenticationUserRepository authenticationUserRepository) {
		return args -> {
			AuthenticationUser authenticationUser =
					new AuthenticationUser("waleed@gmail.com", encoder.encode("password"));
			authenticationUserRepository.save(authenticationUser);
		};
	}
}
