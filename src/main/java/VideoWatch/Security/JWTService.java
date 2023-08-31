package VideoWatch.Security;

import VideoWatch.Model.Customer;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class JWTService {

    // Read the private key from a secure configuration or environment variable
    @Value("${jwt.secretKey}")
    private String secretKey;

    private static final Logger logger = LoggerFactory.getLogger(JWTService.class);

    // The expiration time of the token in seconds (20min)
    private static final long tokenDuration = Duration.ofMinutes(20).getSeconds();

    public String generateToken(Authentication authentication) {

        // Add debug logging to check if the method gets called and see the content of the authentication object.
        logger.debug("generateToken called with authentication: {}", authentication);

        // Further details can be logged to see the principal details if the authentication is not null.
        if (authentication != null && authentication.getPrincipal() != null) {
            logger.debug("Authentication principal: {}", authentication.getPrincipal());
        }

        // the getPrincipal method returns the Object of the authentication
        // that is then converted to the POJO.
        // In this app the POJO is the Customer
        Customer customer = (Customer) authentication.getPrincipal();

        Instant now = Instant.now();
        Instant expirationTime = now.plusSeconds(tokenDuration);

        String token = null;
        try {
            token = Jwts.builder()
                    .setSubject(customer.getEmail()) //specifies what should be used to identify the customer
                    .setIssuedAt(Date.from(now)) //when the token was issued
                    .setExpiration(Date.from(expirationTime))//will end at a specified time
                    .signWith(SignatureAlgorithm.HS512, secretKey)// the algorithm used to create the token and the key used that only the server knows
                    .compact();
            logger.info("JWT Token Generated: " + token); // Logging the generated token
        } catch (Exception e) {
            logger.error("Error during JWT token generation", e); // Logging any exception during token generation
        }
        return token;
    }


    public List<String> getRolesFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        return claims.get("roles", List.class);
    }

    public Optional<String> getUserId(String token) {
        try {
            //the Claims class represents the payload of the JSON web token. It provides the details of the token, like the subject,user role, expiration date, etc
            //by deserializing the payload, the JWT library creats a Claims object with all the claims present in the token.
            Claims claims = parse(token); //parsing divides the JWT string in Header, Payload and Signature
            String subject = claims.getSubject();//get the email claim from the token
            if (subject != null) {
                return Optional.of((subject));
            }
        } catch (Exception e) {

        }
        return Optional.empty();
    }

    public boolean isValid(String token) {
        try {
            // if it doesnt throw an exception, the token is valid.
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            // Token is expired but it was valid
            return false;
        } catch (SignatureException e) {
            // Invalid signature, token has been messed with.
            return false;
        } catch (MalformedJwtException e) {
            // The token is not constructed correctly.
            return false;
        } catch (UnsupportedJwtException e) {
            // The token is unsupported.
            return false;
        } catch (IllegalArgumentException e) {
            // The token is empty, null or not correct.
            return false;
        }
    }

    private Claims parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token) //this method parses the token and if the signature matches it returns a Claims object with the claims and the signature.
                .getBody(); //and then it returns a Claims object with just the JSON payload(claims).
    }
}