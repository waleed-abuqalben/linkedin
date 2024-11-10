package com.linkedin.linkedin.features.authentication.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import org.springframework.stereotype.Component;

@Component
public class Encoder {

	public String encode(String rowString) {
		try {
			MessageDigest digest = 
					MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(rowString.getBytes());
			return Base64.getEncoder().encodeToString(hash);
		}catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Error Encoding string", e);
		}
	}
	
	public boolean matches(String rowString, String encodedString) {
		return encode(rowString).equals(encodedString);
	}
}
