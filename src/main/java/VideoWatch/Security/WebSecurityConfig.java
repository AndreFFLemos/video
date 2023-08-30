package VideoWatch.Security;

import VideoWatch.Service.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
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
public class WebSecurityConfig  {
    @Value("${jwt.secretKey}")
    private String secretKey;
    private final JWTService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    @Autowired
    public WebSecurityConfig(@Lazy JWTService jwtService, CustomUserDetailsService customUserDetailsService) {
        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return new ProviderManager(Arrays.asList(authenticationProvider( customUserDetailsService, passwordEncoder())));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(CustomUserDetailsService customUserDetailsService, PasswordEncoder passwordEncoder) {
        CustomDaoAuthenticationProvider authProvider = new CustomDaoAuthenticationProvider(customUserDetailsService,passwordEncoder);

        authProvider.setUserDetailsService(customUserDetailsService);
        System.out.println(authProvider);

        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider( customUserDetailsService, passwordEncoder()));
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        configureCors(http);
        configureCsrf(http);
        configureSession(http);
        configureAuthorization(http);
        addFilters(http);

        try {
            return http.build();
        } catch (Exception e) {
            // Consider logging the exception and adding more context to the thrown exception.
            throw new RuntimeException("Error building security filter chain.", e);
        }
    }

    private void configureCors(HttpSecurity http) throws Exception {
        http.cors();
    }

    private void configureCsrf(HttpSecurity http) throws Exception {
        http.csrf().disable(); // Disabling CSRF for stateless API
    }

    private void configureSession(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // Make the API stateless
    }

    private void configureAuthorization(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .requestMatchers("/api/login", "/api/register").permitAll()
                .requestMatchers("/api/logout").authenticated()
                .requestMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()
                .anyRequest().authenticated();
    }

    private void addFilters(HttpSecurity http) throws Exception {
        http    .addFilterBefore(requestLoggingFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(authenticationLoggingFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(authenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }


    @Order(1)
    public RequestLoggingFilter requestLoggingFilter() {
        return new RequestLoggingFilter();
    }


    @Order(2)
    public AuthenticationLogginFilter authenticationLoggingFilter() {
        return new AuthenticationLogginFilter();
    }
    @Bean
    @Order(3)
    public JWTAuthenticationFilter jwtAuthenticationFilter() {
        return new JWTAuthenticationFilter(jwtService, customUserDetailsService);
    }


    @Bean
    @Order(4)
    public UsernamePasswordAuthenticationFilter authenticationFilter() throws Exception {
        return new UsernameAuthenticationFilter(authenticationManagerBean());
    }

}