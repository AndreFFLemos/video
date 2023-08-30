package VideoWatch.Security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
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
    protected String obtainUsername(HttpServletRequest request) {
        try {
            InputStream inputStream = request.getInputStream();
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> body = objectMapper.readValue(inputStream, new TypeReference<Map<String, String>>() {});

            String email = body.get("email");
            System.out.println("Extracted Email: " + email);
            return email;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}