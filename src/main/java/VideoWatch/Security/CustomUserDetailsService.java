package VideoWatch.Security;

import VideoWatch.Model.Customer;
import VideoWatch.Service.CustomerService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private CustomerService customerService;

     @Autowired
    public CustomUserDetailsService(@Lazy CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override // from the userdetailsservice
    // the method is invoked during the authentication process
    //it takes an email as input and tries to load the customer based on the email
    public UserDetails loadUserByUsername(String email){
        Optional<Customer> customer= customerService.findCustomerByEmail(email);

        if (customer.isEmpty() || customer==null) {
            throw new UsernameNotFoundException("Customer with email " + email + " was not found.");
        }

        return customer.get();
    }
}
