package VideoWatch.Controller;

import VideoWatch.Model.Email;
import VideoWatch.Model.UserRegistrationRequest;
import VideoWatch.Service.CustomerServiceInterface;
import VideoWatch.Service.EmailServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

    @RestController
    @RequestMapping(value= "/api")
    public class AuthController {

        private CustomerServiceInterface customerServiceInterface;
        private EmailServiceInterface emailServiceInterface;

    @Autowired
    public AuthController(CustomerServiceInterface customerServiceInterface, EmailServiceInterface emailServiceInterface) {
        this.customerServiceInterface = customerServiceInterface;
        this.emailServiceInterface = emailServiceInterface;
    }

    @PostMapping(value = "/register")
        public ResponseEntity<?> register(@RequestBody UserRegistrationRequest registrationRequest) {
            // Check if user already exists
            if(customerServiceInterface.findCustomerByEmail(registrationRequest.getEmail())!=null) {
                return ResponseEntity.badRequest().body("User already exists");
            }

            // Create a new user
            customerServiceInterface.createCustomer(registrationRequest);
            //when a user registers at the API it authomatically sends a welcoming email
            emailServiceInterface.sendWelcomingEmail(registrationRequest);

            // Return a success response
            return ResponseEntity.ok("Registration successful");
        }
    }
