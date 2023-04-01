package com.app.springjwtrest.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtUtil {

    @Value("{SECRET_KEY}")
    private String secret;


    public String getJWT(UserDetails userDetails) {

        Map<String, Object> claims = new HashMap<>();

        return Jwts.builder().setClaims(claims).setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7 * 2))
                .signWith(SignatureAlgorithm.HS256, secret).compact();

    }

    public Boolean isValidToken(String token, UserDetails userDetails) {
        final String userName = extractClaim(token, Claims::getSubject);

        return (userName.equals(userDetails.getUsername()) && !extractClaim(token, Claims::getExpiration).before(new Date()));
    }

 
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        return claimsResolver.apply(claims);
    }

}
