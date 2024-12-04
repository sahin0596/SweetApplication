package az.jrs.sweet.service;

import az.jrs.sweet.model.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import java.security.*;
import java.util.Date;

@Service
public class JwtService {

    private static final KeyPair keyPair = generateKeyPair();
    private static final PublicKey PUBLIC_KEY = keyPair.getPublic();
    private static final PrivateKey PRIVATE_KEY = keyPair.getPrivate();

    private static KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(4096);
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating RSA key pair", e);
        }
    }

    public String generateAccessToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuer("Sweet-Application")
                .claim("roles", user.getRole())
                .claim("userId", user.getId())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(PRIVATE_KEY, SignatureAlgorithm.RS512)
                .compact();
    }

    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuer("Sweet-Application")
                .claim("userId", user.getId())
                .setExpiration(new Date(System.currentTimeMillis() + 604800000))
                .signWith(PRIVATE_KEY, SignatureAlgorithm.RS512)
                .compact();
    }

    public Claims parseToken(String token) {
        JwtParser parser = Jwts.parser()
                .setSigningKey(PUBLIC_KEY)
                .build();
        return parser.parseClaimsJws(token).getBody();
    }
    public boolean isTokenExpired(String token) {
        Claims claims = parseToken(token);
        return claims.getExpiration().before(new Date());
    }

}
