package VideoWatch.Security;

import VideoWatch.Service.CustomerService;
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
    public DaoAuthenticationProvider authenticationProvider(CustomUserDetailsService customUserDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return new ProviderManager(Arrays.asList(authenticationProvider(customUserDetailsService)));
    }

    /*this method waits for post requests being done to the /api/login endpoint with a
     payload containing a username and password, this filter will handle the
     authentication process. If it's successful, the success
     handler provided will execute, sending the generated JWT back to the frontend.
     */
    @Bean
    @Order(4)
    public UsernamePasswordAuthenticationFilter authenticationFilter() {
        try {
            UsernamePasswordAuthenticationFilter filter = new UsernamePasswordAuthenticationFilter();

            // Set filter to expect "email" parameter instead of default "username"
            filter.setUsernameParameter("email");
            //listen to requests here
            filter.setFilterProcessesUrl("/api/login");
            //this manager receives the username and password and then confirms if they are valid, if so returns a auth object
            filter.setAuthenticationManager(authenticationManagerBean());

            /*
            this instance generates a JWT token and writes it directly to the HTTP response. This ensures that,
             after a successful login, the client receives a JWT token, which can be used for subsequent authenticated requests.
             */
            filter.setAuthenticationSuccessHandler((request, response, authentication) -> {
                String jwtToken = jwtService.generateToken(authentication);
                response.getWriter().write(jwtToken);
                response.getWriter().flush();
            });

            return filter;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating authentication filter", e);
        }
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

    private void addFilters(HttpSecurity http) {
        http.addFilterBefore(requestLoggingFilter(), UsernamePasswordAuthenticationFilter.class)
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

}