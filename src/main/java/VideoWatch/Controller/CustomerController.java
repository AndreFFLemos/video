package VideoWatch.Controller;

import VideoWatch.DTO.CustomerDto;
import VideoWatch.DTO.PasswordDto;
import VideoWatch.Model.Email;
import VideoWatch.Model.UserLoginRequest;
import VideoWatch.Model.UserLoginResponse;
import VideoWatch.Service.CustomerServiceInterface;
import VideoWatch.Service.EmailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/customer")
public class CustomerController implements CustomerControllerInterface {

    private CustomerServiceInterface customerServiceInterface;
    private EmailService emailService;

    @Autowired
    public CustomerController(CustomerServiceInterface customerServiceInterface, EmailService emailService) {
        this.customerServiceInterface = customerServiceInterface;
        this.emailService = emailService;
    }

    @Override
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable int id) {

        customerServiceInterface.deleteCustomer(id);

        // the build method constructs a response entity with an empty body
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{id}")
    @Override
    public ResponseEntity<CustomerDto> getCustomer(@PathVariable int id) {

        return new ResponseEntity<>(customerServiceInterface.findCustomerById(id),HttpStatus.OK);
    }
    @Override
    @PutMapping(value = "/update/{id}")
    public ResponseEntity<Void> updateCustomer(@PathVariable int id,@Valid @RequestBody CustomerDto toUpdateCustomerDto) {
        //the browser sends the updated customer data in the body of the request in JSON format.
        customerServiceInterface.updateCustomer(id,toUpdateCustomerDto);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/updatePassword/{id}")
    public ResponseEntity<Void> updatePassword(@PathVariable Integer id, @Valid @RequestBody PasswordDto passwordDto) {
        customerServiceInterface.updatePassword(id,passwordDto);
        // the build method constructs a response entity with an empty body
        return ResponseEntity.noContent().build();
    }

    @Override
    @GetMapping(value = "/byid")
    public ResponseEntity<CustomerDto> findCustomerByID(@RequestParam ("id") int id) {

        return ResponseEntity.ok(customerServiceInterface.findCustomerById(id));
    }

    @Override
    @GetMapping(value = "/byfirstname")
    public ResponseEntity<List<CustomerDto>> findCustomerByFirstName(@Valid @RequestParam("firstname") String firstName) {

        return new ResponseEntity<>(customerServiceInterface.findCustomerByFirstName(firstName),HttpStatus.OK);

    }

    @Override
    @GetMapping (value = "/bylastname")
    public ResponseEntity<List<CustomerDto>> findCustomerByLastName(@Valid @RequestParam ("lastName") String lastName) {
        return new ResponseEntity<>(customerServiceInterface.findCustomerByLastName(lastName),HttpStatus.OK);
    }

    @Override
    @GetMapping(value = "/all")
    public ResponseEntity<List<CustomerDto>> findAllCustomers() {

        return new ResponseEntity<>(customerServiceInterface.findAll(), HttpStatus.OK);
    }
    @Override
    @PostMapping(value="/login")
    public UserLoginResponse loginRequest(@RequestBody UserLoginRequest login) {
        return customerServiceInterface.login(login.getEmail(), login.getPassword());
    }

    @PostMapping(value="/email")
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