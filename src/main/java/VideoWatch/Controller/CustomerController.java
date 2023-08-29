package VideoWatch.Controller;

import VideoWatch.DTO.CustomerDto;
import VideoWatch.DTO.PasswordDto;
import VideoWatch.Model.BlackListedTokens;
import VideoWatch.Model.Email;
import VideoWatch.Model.UserLoginRequest;
import VideoWatch.Model.UserLoginResponse;
import VideoWatch.Service.BlackListedTokenService;
import VideoWatch.Service.BlackListedTokenServiceInterface;
import VideoWatch.Service.CustomerServiceInterface;
import VideoWatch.Service.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.Cookie;

import java.util.List;


@RestController
@RequestMapping(value = "/api")
public class CustomerController implements CustomerControllerInterface {

    private CustomerServiceInterface customerServiceInterface;
    private EmailService emailService;
    private BlackListedTokenServiceInterface blackListedTokenServiceInterface;
    @Value("${security.headerPrefix}")
    private String headerPrefix;


    @Autowired
    public CustomerController(CustomerServiceInterface customerServiceInterface, EmailService emailService) {
        this.customerServiceInterface = customerServiceInterface;
        this.emailService = emailService;
    }

    @Override
    @DeleteMapping(value = "/customer/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable int id) {

        customerServiceInterface.deleteCustomer(id);

        // the build method constructs a response entity with an empty body
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/customer/{id}")
    @Override
    public ResponseEntity<CustomerDto> getCustomer(@PathVariable int id) {

        return new ResponseEntity<>(customerServiceInterface.findCustomerById(id),HttpStatus.OK);
    }
    @Override
    @PutMapping(value = "/customer/update/{id}")
    public ResponseEntity<Void> updateCustomer(@PathVariable int id,@Valid @RequestBody CustomerDto toUpdateCustomerDto) {
        //the browser sends the updated customer data in the body of the request in JSON format.
        customerServiceInterface.updateCustomer(id,toUpdateCustomerDto);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/customer/updatePassword/{id}")
    public ResponseEntity<Void> updatePassword(@PathVariable Integer id, @Valid @RequestBody PasswordDto passwordDto) {
        customerServiceInterface.updatePassword(id,passwordDto);
        // the build method constructs a response entity with an empty body
        return ResponseEntity.noContent().build();
    }

    @Override
    @GetMapping(value = "/customer/byid")
    public ResponseEntity<CustomerDto> findCustomerByID(@RequestParam ("id") int id) {

        return ResponseEntity.ok(customerServiceInterface.findCustomerById(id));
    }

    @Override
    @GetMapping(value = "/customer/byfirstname")
    public ResponseEntity<List<CustomerDto>> findCustomerByFirstName(@Valid @RequestParam("firstname") String firstName) {

        return new ResponseEntity<>(customerServiceInterface.findCustomerByFirstName(firstName),HttpStatus.OK);

    }

    @Override
    @GetMapping (value = "/customer/bylastname")
    public ResponseEntity<List<CustomerDto>> findCustomerByLastName(@Valid @RequestParam ("lastName") String lastName) {
        return new ResponseEntity<>(customerServiceInterface.findCustomerByLastName(lastName),HttpStatus.OK);
    }

    @Override
    @GetMapping(value = "/customer/all")
    public ResponseEntity<List<CustomerDto>> findAllCustomers() {

        return new ResponseEntity<>(customerServiceInterface.findAll(), HttpStatus.OK);
    }

    /*backend will store the authentication token in an HttpOnly cookie
    instead of sending it in the response body. The frontend automatically
   attachs the cookie to subsequent requests to the server,
    which can then extract the token and verify it. The httpservlet is an API that sends
    the responses and Spring injects it in the method to be manipulated and add the cookie
     */
    @Override
    @CrossOrigin(origins = "http://localhost:5500/api/login", allowCredentials = "true")
    @PostMapping(value="/login")
    public UserLoginResponse loginRequest(@RequestBody UserLoginRequest login, HttpServletResponse response) {
        UserLoginResponse userLoginResponse = customerServiceInterface.login(login.getEmail(), login.getPassword());
/*
        // Create HttpOnly Cookie with the token
        Cookie authCookie = new Cookie("auth_token", userLoginResponse.getToken());
        authCookie.setHttpOnly(true);
        authCookie.setSecure(false);  // This should be set when using HTTPS
        authCookie.setPath("/");     // Available to entire domain

        response.addCookie(authCookie);
*/
        return new UserLoginResponse(userLoginResponse.getCustomerDto());
    }

    /* this is the endpoint where the browser can retrieve the csrf token it's not needed anymore because
    in a stateless design using JWT, the client will send the JWT token with every request,
    which reduces CSRF attacks because the token is not automatically sent by browsers.
    @GetMapping("/csrf-token")
    public ResponseEntity<String> getCSRFToken(HttpServletRequest request) {
        System.out.println("Received request with method: " + request.getMethod());
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("X-CSRF-TOKEN", csrfToken.getToken());
            return new ResponseEntity<>("", headers, HttpStatus.OK);
        } else {
            System.out.println("csrfToken is null!");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("CSRF token not found.");
    }*/

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization").replace(headerPrefix, "");

        // Add token to the blacklist when token is terminated before the expiracy date
        blackListedTokenServiceInterface.addTokenToBlacklist(token);

        // Clear the current authentication
        SecurityContextHolder.getContext().setAuthentication(null);

        return ResponseEntity.ok("Logged out successfully");
    }


    @PostMapping(value="/customer/email")
    public String sendEmail(@RequestBody Email email){
        try {
            emailService.sendEmail(email);
            return "it sended";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }
    }
}