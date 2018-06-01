package com.accenture.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.accenture.entity.Users;
import com.accenture.service.CustomerService;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
	private final CustomerService customerService;
	private final PasswordEncoder encoder;

	// Static variable to capture national Id when log in
	private static String id;

	@Autowired
	public CustomAuthenticationProvider(CustomerService customerService, PasswordEncoder encoder) {
		this.customerService = customerService;
		this.encoder = encoder;
	}

	// Method will take the login parameter as input and compare it with the value
	// in database
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String nationalId = authentication.getName();
		String password = authentication.getCredentials().toString();
		id = nationalId;
		Users user = customerService.searchById(nationalId);
		if (user != null) {
			// Get User's roles
			List<GrantedAuthority> authorities = getAuthorities(user);

			// Check password
			String encodedPassword = user.getPassword();
			if (encoder.matches(password, encodedPassword)) {
				// Return the valid authentication that will be stored in SecurityContext
				return new UsernamePasswordAuthenticationToken(nationalId, encodedPassword, authorities);
			}

		}
		throw new BadCredentialsException("Invalid credentials");
	}

	// Convert Roles into GrantedAuthority
	private List<GrantedAuthority> getAuthorities(Users user) {
		List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
		grantedAuthorities.add(new SimpleGrantedAuthority(user.getRole().toString()));

		return grantedAuthorities;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

	public static String getId() {
		return id;
	}

	public static void setId(String id) {
		CustomAuthenticationProvider.id = id;
	}
}