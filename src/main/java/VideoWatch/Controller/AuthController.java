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
            Email email= new Email();
            email.setSubject("Welcome to Blockbuster");
            email.setBody("Thank you for registering at Blockbuster");
            email.setSender("paisagensagua@gmail.com");
            //So there is a method or attribute that expects a list, but I only want to pass a single item
            // i can use the collections.singleton to create a list with one item
            email.setReceivers(Collections.singletonList(registrationRequest.getEmail()));
            try {
                emailServiceInterface.sendEmail(email);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Return a success response
            return ResponseEntity.ok("Registration successful");
        }
    }
