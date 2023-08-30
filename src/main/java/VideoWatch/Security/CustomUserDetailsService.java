package VideoWatch.Security;

import VideoWatch.Model.Customer;
import VideoWatch.Repository.CustomerRepository;
import VideoWatch.Service.CustomerService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private CustomerRepository customerRepository;

    @Autowired
    public CustomUserDetailsService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }




    @Override // from the userdetailsservice
    // the method is invoked during the authentication process
    //it takes an email as input and tries to load the customer based on the email
    public UserDetails loadUserByUsername(String email){
        Optional<Customer> customer= customerRepository.findByEmail(email);

        if (customer.isEmpty()) {
            throw new UsernameNotFoundException("Customer with email " + email + " was not found.");
        }

        return customer.get();

    }
}
