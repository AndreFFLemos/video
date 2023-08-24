package VideoWatch.Security;

import VideoWatch.Service.CustomerService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;

import java.util.Arrays;

import static org.springframework.security.config.Customizer.withDefaults;


//this class provides the config for spring security
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    @Value("${jwt.secretKey}")
    private String secretKey;
    private final JWTService jwtService;
    private final ModelMapper modelMapper;
    private final CustomUserDetailsService customUserDetailsService;

    public WebSecurityConfig(JWTService jwtService, ModelMapper modelMapper, @Lazy CustomUserDetailsService customUserDetailsService) {
        this.jwtService = jwtService;
        this.modelMapper = modelMapper;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CustomUserDetailsService customUserDetailsService(CustomerService customerService) {
        return new CustomUserDetailsService(modelMapper, customerService);
    }

    //this retrieves the user details and compares the password of the user with the provided password
    @Bean
    public DaoAuthenticationProvider authenticationProvider(CustomUserDetailsService customUserDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    //it filters the requests to the URL to ensure the user is authenticated
    @Bean
    public JWTAuthenticationFilter jwtAuthenticationFilter() {
        return new JWTAuthenticationFilter(jwtService, customUserDetailsService);

    }
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return new ProviderManager(Arrays.asList(authenticationProvider(customUserDetailsService)));
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()  // Disabling CSRF for stateless API
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // Make the API stateless
                .and()
                .authorizeRequests()
                //this will allow non-auth users to use the token in the header when they submit the login form
                .requestMatchers("/api/csrf-token").permitAll()
                .requestMatchers("/api/login").permitAll()
                .requestMatchers("/api/register").permitAll()  // Public access
                .requestMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()
                .anyRequest().authenticated() // All other requests need authentication
                .and()
                .addFilterBefore(jwtAuthenticationFilter(),
                        UsernamePasswordAuthenticationFilter.class)
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                );
        return http.build();
    }

    private CsrfTokenRepository csrfTokenRepository() {
        return new CustomCsrfTokenRepository();
    }
}

