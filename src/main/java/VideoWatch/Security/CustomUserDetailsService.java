package VideoWatch.Security;

import VideoWatch.Model.Customer;
import VideoWatch.Service.CustomerService;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public class CustomUserDetailsService implements UserDetailsService {

    private ModelMapper modelMapper;
    private CustomerService customerService;

    public CustomUserDetailsService(ModelMapper modelMapper, CustomerService customerService) {
        this.modelMapper = modelMapper;
        this.customerService = customerService;
    }
    public CustomUserDetailsService(){

    }

    @Override // from the userdetailsservice
    // the method is invoked during the authentication process
    //it takes an email as input and tries to load the customer based on the email
    public UserDetails loadUserByUsername(String email){
        Optional<Customer> customer= customerService.findCustomerByEmail(email);

        if (customer.isEmpty()) {
            throw new UsernameNotFoundException("Customer with email " + email + " was not found.");
        }

        return customer.get();
    }
}
