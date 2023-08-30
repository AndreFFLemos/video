package VideoWatch.Controller;

import VideoWatch.Model.Email;
import VideoWatch.Model.UserRegistrationRequest;
import VideoWatch.Service.CustomerServiceInterface;
import VideoWatch.Service.EmailServiceInterface;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

import static org.hibernate.query.sqm.tree.SqmNode.log;

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

            try {
                // Create a new user
                customerServiceInterface.createCustomer(registrationRequest);

                //when a user registers at the API it authomatically sends a welcoming email
                emailServiceInterface.sendWelcomingEmail(registrationRequest);

                //the Map.of creates an immutable collection and cannot be changed
                Map<String, String> data = Map.of(
                        "status", "Registration succeeded");

                return ResponseEntity.status(HttpStatus.SC_CREATED).body(data);
            } catch (Exception e) {
                // log the error and return a response
                log.error("Error during registration", e);
                return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body("Registro falhado por erros internos");
            }
        }
}