package com.project.auction.controller;

import java.util.stream.Collectors;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.project.auction.service.UserDetailsImpl;
import com.project.auction.models.User;
import com.project.auction.repository.UserRepository;
import com.project.auction.pojo.LoginRequest;
import com.project.auction.pojo.SignupRequest;
import com.project.auction.pojo.MessageResponse;

@RestController
public class AuthController {

    @Autowired
	AuthenticationManager authenticationManager;

    @Autowired
	UserRepository userRespository;

    @Autowired
	PasswordEncoder passwordEncoder;
    
    @PostMapping("/signin")
	public ResponseEntity<?> authUser(@RequestBody LoginRequest loginRequest) {
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(
						loginRequest.getUsername(), 
						loginRequest.getPassword()));
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		// String jwt = jwtUtils.generateJwtToken(authentication);
		
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		List<String> roles = userDetails.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.collect(Collectors.toList());
		
		// return ResponseEntity.ok(new JwtResponse(jwt, 
		// 		userDetails.getId(), 
		// 		userDetails.getUsername(), 
		// 		userDetails.getEmail(), 
		// 		roles));
        return ResponseEntity.ok(new MessageResponse("Success"));
	}

    @PostMapping("/signup")
	public ResponseEntity<?> registerUser(@RequestBody SignupRequest signupRequest) {
		
		if (userRespository.existsByEmail(signupRequest.getEmail())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Email is exist"));
		}
		
		User user = new User(signupRequest.getName(), 
				signupRequest.getEmail(),
				passwordEncoder.encode(signupRequest.getPassword()));
		
		userRespository.save(user);
		return ResponseEntity.ok(new MessageResponse("User CREATED"));
	}
}
