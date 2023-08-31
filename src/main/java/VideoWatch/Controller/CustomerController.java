package VideoWatch.Controller;

import VideoWatch.DTO.CustomerDto;
import VideoWatch.DTO.PasswordDto;
import VideoWatch.Model.*;
import VideoWatch.Security.JWTService;
import VideoWatch.Service.BlackListedTokenService;
import VideoWatch.Service.BlackListedTokenServiceInterface;
import VideoWatch.Service.CustomerServiceInterface;
import VideoWatch.Service.EmailService;
import ch.qos.logback.classic.Logger;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.Cookie;

import java.util.List;
import java.util.NoSuchElementException;


@RestController
@RequestMapping(value = "/api")
public class CustomerController implements CustomerControllerInterface {

    private CustomerServiceInterface customerServiceInterface;
    private EmailService emailService;
    private BlackListedTokenServiceInterface blackListedTokenServiceInterface;
    private JWTService jwtService;
    private ModelMapper modelMapper;
    @Value("${security.headerPrefix}")
    private String headerPrefix;
    private static final Logger logger = (Logger) LoggerFactory.getLogger(CustomerController.class);


    @Autowired
    public CustomerController(CustomerServiceInterface customerServiceInterface, EmailService emailService, JWTService jwtService, ModelMapper modelMapper) {
        this.customerServiceInterface = customerServiceInterface;
        this.emailService = emailService;
        this.jwtService = jwtService;
        this.modelMapper = modelMapper;
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

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization").replace("Bearer ", "");

        logger.info("User logged out." + token);

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