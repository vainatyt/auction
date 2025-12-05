package com.project.auction.config.jwt;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.project.auction.service.UserDetailsServiceImpl;

public class AuthTokenFilter extends OncePerRequestFilter {
	
	@Autowired
	private JwtUtils jwtUtils;
	
	@Autowired
	private UserDetailsServiceImpl userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		System.out.println("Start jwt filter");
		try {
			String jwt = parseJwt(request);
			if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
				String username = jwtUtils.getUserNameFromJwtToken(jwt);
				
				UserDetails userDetails = userDetailsService.loadUserByUsername(username);
				UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				
				SecurityContextHolder.getContext().setAuthentication(authenticationToken);
			}
		}catch (Exception e) {
			System.err.println(e);
		}
		filterChain.doFilter(request, response);
		System.out.println("end jwt filter");
	}

	private String parseJwt(HttpServletRequest request) {
	    String headerAuth = request.getHeader("Authorization");
		System.out.println("parseJwt ");
	    if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
			System.out.println(headerAuth.substring(7, headerAuth.length()));
	      return headerAuth.substring(7, headerAuth.length());
	    }
		System.out.println("null");
	    return null;
	  }
}