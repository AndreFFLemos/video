package VideoWatch.Controller;

import VideoWatch.Model.Email;
import VideoWatch.Model.UserRegistrationRequest;
import VideoWatch.Service.CustomerServiceInterface;
import VideoWatch.Service.EmailServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@RestController
    @RequestMapping(value= "/api")
    @CrossOrigin(origins = "http://127.0.0.1:5500")
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
            if(customerServiceInterface.findCustomerByEmail(registrationRequest.getEmail())==null) {
                return ResponseEntity.noContent().build();
            }

            // Create a new user
            customerServiceInterface.createCustomer(registrationRequest);
            //when a user registers at the API it authomatically sends a welcoming email
            emailServiceInterface.sendWelcomingEmail(registrationRequest);

            //the Map.of creates an immutable collection and cannot be changed
        Map<String, String> data = Map.of(
                "status", "redirection_required",
                "message", "Please continue to the URL.",
                "redirect_url", "http://localhost:8080/api/login"
        );

        return ResponseEntity.ok(data);
        }
    }
