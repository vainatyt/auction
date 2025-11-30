package com.project.auction.config.jwt;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.project.auction.service.UserDetailsImpl;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JwtUtils {

	@Value("${app.jwtSecret}")
	private String jwtSecret;

	@Value("${app.jwtExpirationMs}")
	private int jwtExpirationMs;

    private SecretKey key;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

	public String generateJwtToken(Authentication authentication) {

		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

		return Jwts.builder().subject((userPrincipal.getUsername())).issuedAt(new Date())
				.expiration(new Date((new Date()).getTime() + jwtExpirationMs))
				.signWith(key, Jwts.SIG.HS512).compact();
	}

	public boolean validateJwtToken(String jwt) {
		try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(jwt);
			return true;
		} catch (MalformedJwtException e) {
			System.err.println(e.getMessage());
		} catch (IllegalArgumentException e) {
			System.err.println(e.getMessage());
		}

		return false;
	}

	public String getUserNameFromJwtToken(String jwt) {
		return Jwts.parser().verifyWith(key).build().parseSignedClaims(jwt).getPayload().getSubject();
	}

}