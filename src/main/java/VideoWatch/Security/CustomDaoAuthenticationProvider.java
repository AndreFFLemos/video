package VideoWatch.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomDaoAuthenticationProvider extends DaoAuthenticationProvider {

    @Autowired
    public CustomDaoAuthenticationProvider(CustomUserDetailsService customUserDetailsService, PasswordEncoder passwordEncoder) {
        setUserDetailsService(customUserDetailsService);
        setPasswordEncoder(passwordEncoder);
    }
        @Override
        protected void additionalAuthenticationChecks(UserDetails userDetails,
                                                      UsernamePasswordAuthenticationToken authentication)
                throws AuthenticationException {

            String rawPasswordFromLoginForm = (String) authentication.getCredentials();
            String hashedPasswordFromDb = userDetails.getPassword();

            // Logging for debugging purposes ONLY
            System.out.println("Raw Password from login: " + rawPasswordFromLoginForm);
            System.out.println("Hashed Password from DB: " + hashedPasswordFromDb);

            // Continue with the default authentication checks
            super.additionalAuthenticationChecks(userDetails, authentication);
        }
    }


