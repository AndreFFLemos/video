package VideoWatch.Security;

import VideoWatch.Model.UserLoginRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;


public class UsernameAuthenticationFilter extends UsernamePasswordAuthenticationFilter {


    private final JWTService jwtService;
    private static final Logger logger = LoggerFactory.getLogger(JWTService.class);

    @Autowired
    public UsernameAuthenticationFilter(AuthenticationManager authenticationManager, JWTService jwtService) {
        super.setAuthenticationManager(authenticationManager);
        this.jwtService = jwtService;
        setUsernameParameter("email");
        setFilterProcessesUrl("/api/login");

        setAuthenticationSuccessHandler((request, response, authentication) -> {
            try {
                String jwtToken = jwtService.generateToken(authentication);

                // Logging the successful authentication
                System.out.println("User " + authentication.getName() + " was successfully authenticated.");

                // Logging the token generation
                System.out.println("Generated JWT Token: " + jwtToken);

                // Construct JSON response with token because frontend is expecting token in json
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"token\":\"" + jwtToken + "\"}");
                response.getWriter().flush();
            } catch(Exception e) {
                e.printStackTrace();  // or log using a logger
            }
        });
    }

    /*Before any of the other methods get a chance to run, it reads the InputStream,
    deserializes it into a UserLoginRequest, and then sets it as a request attribute.
     The obtainUsername and obtainPassword methods then simply pull from this attribute
      instead of attempting to re-read the request's InputStream.
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            System.out.println("Login attempt detected. Processing authentication for /api/login endpoint.");
            UserLoginRequest userLoginRequest = new ObjectMapper().readValue(request.getInputStream(), UserLoginRequest.class);
            request.setAttribute("userLoginRequest", userLoginRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return super.attemptAuthentication(request, response);
    }



    @Override
    protected String obtainUsername(HttpServletRequest request) {
        UserLoginRequest userLoginRequest = (UserLoginRequest) request.getAttribute("userLoginRequest");
        String email = userLoginRequest.getEmail();
        logger.info("Extracted Email: " + email);
        return email;
    }

    @Override
    protected String obtainPassword(HttpServletRequest request) {
        UserLoginRequest userLoginRequest = (UserLoginRequest) request.getAttribute("userLoginRequest");
        String password = userLoginRequest.getPassword();
        logger.info("Extracted Password: " + password);
        return password;
    }
}

