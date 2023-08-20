package VideoWatch.Service;

import VideoWatch.DTO.CustomerDto;
import VideoWatch.DTO.PasswordDto;
import VideoWatch.Model.Customer;
import VideoWatch.Model.UserLoginResponse;
import VideoWatch.Model.UserRegistrationRequest;

import java.util.List;
import java.util.Optional;

public interface CustomerServiceInterface {

    CustomerDto createCustomer(UserRegistrationRequest userRegistration);
    void deleteCustomer(int id);
    CustomerDto findCustomerById(int id);
    void updateCustomer(int id,CustomerDto customerDto);
    List <CustomerDto> findAll();
    List <CustomerDto> findCustomerByFirstName(String name);
    List <CustomerDto> findCustomerByLastName(String l);
    Optional<Customer> findCustomerByEmail(String email);
    UserLoginResponse login(String email, String password);
    void updatePassword(Integer id, PasswordDto passwordDto);
}
