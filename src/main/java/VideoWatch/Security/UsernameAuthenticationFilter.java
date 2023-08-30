package VideoWatch.Security;

import VideoWatch.Model.UserLoginRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.modelmapper.ModelMapper;
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
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;

public class UsernameAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private JWTService jwtService;

    public UsernameAuthenticationFilter(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
        setUsernameParameter("email");
        setFilterProcessesUrl("/api/login");
    }


    public void setJwtService(JWTService jwtService) {
        this.jwtService = jwtService;
        setAuthenticationSuccessHandler((request, response, authentication) -> {
            String jwtToken = jwtService.generateToken(authentication);
            response.getWriter().write(jwtToken);
            response.getWriter().flush();
        });
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
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
        System.out.println("Extracted Email: " + email);
        return email;
    }

    @Override
    protected String obtainPassword(HttpServletRequest request) {
        UserLoginRequest userLoginRequest = (UserLoginRequest) request.getAttribute("userLoginRequest");
        String password = userLoginRequest.getPassword();
        System.out.println("Extracted Password: " + password);
        return password;
    }
}

