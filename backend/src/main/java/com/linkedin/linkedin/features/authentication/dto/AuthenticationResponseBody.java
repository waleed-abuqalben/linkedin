package com.linkedin.linkedin.features.authentication.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AuthenticationResponseBody {
	private String token;
	private String message;
	

}
