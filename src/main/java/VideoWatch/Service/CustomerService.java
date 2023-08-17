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
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class CustomerService implements CustomerServiceInterface {

    private static final String headerPrefix= "Bearer";
    private final CustomerRepository cr;
    private UserRegistrationRequest userRegistration;
    private final ModelMapper modelMapper;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    @Autowired
    private JWTService jwtService;

    @Autowired
    public CustomerService(CustomerRepository cr, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.cr = cr;
        this.modelMapper = modelMapper;
        this.passwordEncoder=passwordEncoder;
    }

    @Autowired
    public void setAuthenticationManager(@Lazy AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public CustomerDto createCustomer(UserRegistrationRequest userRegistration) {
        if (userRegistration == null) {
            throw new IllegalArgumentException("user is null");
        }

        if(userRegistration.getPassword() == null || userRegistration.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password is missing or empty");
        }
        //if the customer is in the DB then the method will throw the exception
        cr.findByEmail(userRegistration.getEmail())
                .ifPresent(customer->new NoSuchElementException("Customer already Exists"));

        //convert the customerDto instance to a POJO instance and save the latter to the customer instance
        Customer customer= modelMapper.map(userRegistration, Customer.class);

        //passwordEncoder criptographs the password introduced by the customer
        String thePassword=passwordEncoder.encode(customer.getPassword());
        customer.setPassword(thePassword);
        //tell the repository to persist the customer instance and save that instance on the customer variable
        customer= cr.save(customer);

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
    public Customer findCustomerByEmail(String email) {

        return cr.findByEmail(email)
                .orElseThrow(()->new NoSuchElementException("No user found"));
    }

    public UserLoginResponse login(String email, String password){

        //the authentication manager gets the login values and if they match an existent user it checks its authentication
        Authentication authentication= authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email,password,Collections.emptyList()));

        //and now Spring knows there is an authenticated user
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token= headerPrefix+jwtService.generateToken(authentication);

        CustomerDto customerDto= cr.findByEmail(email)//returns an optional
                .map(customer->modelMapper.map(customer,CustomerDto.class))//if there is a customer inside the optional it will be mapped
                .orElseThrow(()-> new NoSuchElementException("User not found")); //using a Supplier Interface

        return new UserLoginResponse(token,customerDto);
    }


}