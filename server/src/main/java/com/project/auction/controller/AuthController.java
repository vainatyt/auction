package com.project.auction.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.auction.service.UserDetailsImpl;
import com.project.auction.config.jwt.JwtUtils;
import com.project.auction.models.User;
import com.project.auction.repository.UserRepository;
import com.project.auction.pojo.LoginRequest;
import com.project.auction.pojo.SignupRequest;
import com.project.auction.pojo.MessageResponse;
import com.project.auction.pojo.JwtResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
	AuthenticationManager authenticationManager;

    @Autowired
	UserRepository userRespository;

    @Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	JwtUtils jwtUtils;
    
    @PostMapping("/signin")
	public ResponseEntity<?> authUser(@RequestBody LoginRequest loginRequest) {
		System.out.println("start signin");
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(
						loginRequest.getUsername(), 
						loginRequest.getPassword()));
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		
		return ResponseEntity.ok(new JwtResponse(jwt, 
				userDetails.getUsername(), 
				userDetails.getEmail()));
	}

    @PostMapping("/signup")
	public ResponseEntity<?> registerUser(@RequestBody SignupRequest signupRequest) {
		System.out.println("start signup");
		if (userRespository.existsByName(signupRequest.getUsername())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Username is exist"));
		}

		if (userRespository.existsByEmail(signupRequest.getEmail())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Email is exist"));
		}
		
		User user = new User(signupRequest.getUsername(), 
				passwordEncoder.encode(signupRequest.getPassword()),
				signupRequest.getEmail());
		
		userRespository.save(user);

		return ResponseEntity.ok(new MessageResponse("User CREATED"));
	}
}
