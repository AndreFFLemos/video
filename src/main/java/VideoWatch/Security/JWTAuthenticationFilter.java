package VideoWatch.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


//before the request enters our backend point on the controller, it will hit this class
//responsible for the authentication of the users in every request
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    @Value("${jwt.secretKey}")
    private String secretKey;

    //used to process the jwt token
    private JWTService jwtService;
    //used to load the user details
    private CustomUserDetailsService customUserDetailsService;

    public JWTAuthenticationFilter(JWTService jwtService, CustomUserDetailsService customUserDetailsService) {
        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override //from onceperrequestfilter
    //the method checks the token in the request, verifies the user and sets up the security context
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = getToken(request);

        //from the token, let's get the identification
        Optional<String> userEmail = jwtService.getUserId(token);

        if (userEmail.isPresent()) {

            //with the email get the user from the DB
            UserDetails customer = customUserDetailsService.loadUserByUsername(userEmail.get());
            // Get the roles from the JWT token
            List<String> roles = jwtService.getRolesFromToken(token);

            // Convert the roles to GrantedAuthority objects
            List<GrantedAuthority> authorities = roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.toList());

            //the empty collections parameter are the permissions that the user has, and if the user is authenticated
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(customer, null, authorities);

            //sets the authentication context to the actual request in course
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            //now spring security identify the user
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    //this method extracts the token from the authorization header of the request
    private String getToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        //Spring class with a method to check if the token came with more than white spaces or not
        //the token returns a String but the first 6 letters are Bearer, I want it to return what's next
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            return token.substring(7);
        }

        return null;
    }
}
