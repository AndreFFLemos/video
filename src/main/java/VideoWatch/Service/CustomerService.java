package VideoWatch.Service;

import VideoWatch.DTO.CustomerDto;
import VideoWatch.DTO.PasswordDto;
import VideoWatch.Model.UserLoginResponse;
import VideoWatch.Model.UserRegistrationRequest;
import VideoWatch.Repository.CustomerRepository;
import VideoWatch.Model.Customer;
import VideoWatch.Security.JWTService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class CustomerService implements CustomerServiceInterface {

    @Value("${security.headerPrefix}")
    private String headerPrefix;
    private final CustomerRepository cr;
    private final ModelMapper modelMapper;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public CustomerService(CustomerRepository cr, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.cr = cr;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public CustomerDto createCustomer(UserRegistrationRequest userRegistration) {
        if (userRegistration == null) {
            throw new IllegalArgumentException("user is null");
        }

        if(userRegistration.getPassword() == null || userRegistration.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password is missing or empty");
        }

        if(userRegistration.getPasswordConfirm() == null || userRegistration.getPasswordConfirm().isEmpty()) {
            throw new IllegalArgumentException("PasswordConfirmation is missing or empty");
        }

        //provide extra security in case someone overpasses the frontend validation
        if (!userRegistration.getPassword().equals(userRegistration.getPasswordConfirm())){
            throw new IllegalArgumentException("Passwords not the same");
        }

        //if the customer is in the DB then the method will throw the exception
        cr.findByEmail(userRegistration.getEmail())
                .ifPresent(customer->{
                    throw new IllegalArgumentException("Customer already Exists");
                });

        //convert the customerDto instance to a POJO instance and save the latter to the customer instance
        Customer customer= modelMapper.map(userRegistration, Customer.class);

        //passwordEncoder criptographs the password introduced by the customer
        String thePassword=passwordEncoder.encode(customer.getPassword());
        customer.setPassword(thePassword);
        //tell the repository to persist, imediatelly before the transation is over,
        // the customer instance and save that instance on the customer variable
        customer= cr.saveAndFlush(customer);
        //convert that persisted instance back in to a DTO object
        CustomerDto customerDto1= modelMapper.map(customer,CustomerDto.class);

        return  customerDto1;
    }
    @Override
    public void deleteCustomer(int id) {

        cr.findById(id).orElseThrow(()-> new NoSuchElementException("No customer found"));
    cr.deleteById(id);
    }

    public CustomerDto findCustomerById(int id) {

        return cr.findById(id)
                .map(customer->modelMapper.map(customer, CustomerDto.class))
                .orElse(null);
    }
    @Override
    public void updateCustomer(int id, CustomerDto customerDto) {
        Optional<Customer> existingOptCustomer= cr.findById(id);
        if (existingOptCustomer!=null) {
            //get the instance customer from the optional
            Customer persistedCustomer = existingOptCustomer.get();

            //convert the customerDto instance into a customer instance and save it
            Customer customer = modelMapper.map(customerDto, Customer.class);
            //because the customerDto doesn't have a password attribute then use the existing pass
            String thePassword = passwordEncoder.encode(persistedCustomer.getPassword());
            customer.setPassword(thePassword);
            //customer.setId(id);
            customer.setId(persistedCustomer.getId());
            cr.save(customer);
        }
        else {
            throw new NoSuchElementException("Customer not found");
        }

    }
    @Override
    public void updatePassword(Integer id, PasswordDto passwordDto) {

        Optional<Customer> existingOptCustomer= cr.findById(id);
        Customer customerUpdated= existingOptCustomer.get();

        String thePassword=passwordEncoder.encode(passwordDto.getPassword());
        customerUpdated.setPassword(thePassword);

        cr.save(customerUpdated);
    }

    @Override
    public List <CustomerDto> findAll() {

        //convert the list of customers found in the DB into a List of CustomersDto and return it
           return cr.findAll()
                    .stream()
                    .map(customer -> modelMapper.map(customer, CustomerDto.class))
                    .collect(Collectors.toList());
    }

    @Override
    public List <CustomerDto> findCustomerByFirstName(String firstName) {

        return cr.findByFirstName(firstName)
                .orElse(Collections.emptyList())
                .stream()
                .map(customer -> modelMapper.map(customer,CustomerDto.class))
                .collect(Collectors.toList());

    }

    @Override
    public List<CustomerDto> findCustomerByLastName(String lastName) {

            return cr.findByLastName(lastName)
                    .orElse(Collections.emptyList()) // if the name doesn't return customers, use an empty list and the optional is out
                    .stream()
                    .map(customer -> modelMapper.map(customer, CustomerDto.class))
                    .collect(Collectors.toList());
        }

    @Override
    public Optional<Customer> findCustomerByEmail(String email) {

       Optional<Customer> optionalCustomer=cr.findByEmail(email);
       if (optionalCustomer.isEmpty()){
           return Optional.empty();
       }

       return optionalCustomer;
    }

}