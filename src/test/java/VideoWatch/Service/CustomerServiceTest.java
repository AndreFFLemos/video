package VideoWatch.Service;

import VideoWatch.DTO.CustomerDto;
import VideoWatch.Model.Customer;
import VideoWatch.Model.Movie;
import VideoWatch.Model.UserRegistrationRequest;
import VideoWatch.Repository.CustomerRepository;
import VideoWatch.Validation.CustomerValidator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {
    private PasswordEncoder passwordEncoder;
    private UserRegistrationRequest userRegistrationRequest;
    @Mock
    CustomerRepository cr;
    @InjectMocks
    private CustomerService cs;
    private Validator validator=new CustomerValidator();
    private CustomerDto customerDto;
    private Customer customer;

    private Movie movie;
    //the modelMapper is a class instance
    private static ModelMapper modelMapper;
    private List <Customer> customersFound;
    private CustomerDto customerDtoSaved;

    @BeforeAll
    public static void setupModelMapper() {
    modelMapper = new ModelMapper();
    }

    @BeforeEach
    public void setup(){
        movie=new Movie();
        userRegistrationRequest= new UserRegistrationRequest();
        userRegistrationRequest.setPassword("ugabugas");
        userRegistrationRequest.setEmail("b@b");
        passwordEncoder=new BCryptPasswordEncoder();
        //I'm passing this modelmapper instance to the customer service instance so that the modelmapper is not null
        //I'm using new because the model mapper is not a mock
        cs = new CustomerService(cr, modelMapper,passwordEncoder);
        customerDto=new CustomerDto("A","L","AL","a@l");
        customer=new Customer(1,"A","L","AL",null,"a@l", new LinkedList<>());
        customerDtoSaved = modelMapper.map(customer, CustomerDto.class);// convert the POJO persisted Customer to CustomerDto
        customersFound= new LinkedList<>();
        customersFound.add(customer);
    }

    @Test
    void createCustomerTest() {

        // test when customer doesn't exist
            when(cr.findByEmail("b@b")).thenReturn(Optional.empty());  // There's no customer with this number
            when(cr.save(any(Customer.class))).thenReturn(customer); // By saving a customer, return the predefined customer
            CustomerDto mockedC = cs.createCustomer(userRegistrationRequest);
            assertEquals(customerDtoSaved, mockedC);  // Is the returned dto the same as the saved one?

            //test when customer already exists
            when(cr.findByEmail("b@b")).thenReturn(Optional.of(customer)); //When the customer already exists
            assertThrows(IllegalArgumentException.class, ()-> cs.createCustomer(userRegistrationRequest)); //invoke the cr.findById and then returns empty as defined in the customerService

            verify(cr).save(any(Customer.class)); //the number of times the cr.save method is really used.
            verify(cr,times(2)).findByEmail(anyString());
    }

    @Test
    void deleteCustomerTest() {
        when(cr.findById(anyInt())).thenReturn(Optional.of(customer)); // simulates the existence of the customer
        doNothing().when(cr).deleteById(1); //when the delete method is invoked, do nothing because it returns a void

        cs.deleteCustomer(1);

        verify(cr).deleteById(1);
        verify(cr).findById(anyInt());

    }

    @Test
    void findCustomerByIdTest() {

        when(cr.findById(anyInt())).thenReturn(Optional.of(customer));// it returns a container with a possible object
        CustomerDto mockedC= cs.findCustomerById(0);

        assertNotNull(mockedC); //checks if there is an instance
        assertEquals(customerDtoSaved,mockedC);

        verify(cr).findById(any());

    }
    @Test
    void findCustomerByFirstNameTest(){

        //check when customer is present
        when(cr.findByFirstName("A")).thenReturn(Optional.of(customersFound)); //when the repository method is invoked, return the list
        List <CustomerDto> mockedC= cs.findCustomerByFirstName("A"); //save the results of the search
        assertEquals(1,mockedC.size());

        //check when customer is not present
        when(cr.findByFirstName(anyString())).thenReturn(Optional.of(Collections.emptyList())); //when the name doesnt return customers, return empty container
        List<CustomerDto> customerNotFound= cs.findCustomerByFirstName("T");
        assertTrue(customerNotFound.isEmpty());

        verify(cr).findByFirstName("A");
        verify(cr).findByFirstName("T"); //verify that the method was used with an A and then with a T
    }

    @Test
    void findCustomerByLastNameTest(){

        //check when customer is present
        when(cr.findByLastName("L")).thenReturn(Optional.of(customersFound));
        List <CustomerDto> mockedC= cs.findCustomerByLastName("L");
        assertEquals(1,mockedC.size());

        //check when customer is not present
        when(cr.findByLastName("T")).thenReturn(Optional.of(Collections.emptyList()));
        List<CustomerDto> customerNotFound= cs.findCustomerByLastName("T");
        assertTrue(customerNotFound.isEmpty());

        verify(cr).findByLastName("L");
        verify(cr).findByLastName("T");
    }
    @Test
    void findCustomerByEmailTest (){

        //check when customer is present
        when(cr.findByEmail("a@l")).thenReturn(Optional.of(customer));
        Customer mockedC= cs.findCustomerByEmail("a@l");
        assertEquals(customer,mockedC);

        //check when customer is not present
        when(cr.findByEmail("b@b")).thenReturn(Optional.empty());
        Customer customerNotFound= cs.findCustomerByEmail("b@b");
        assertNull(customerNotFound);

        verify(cr).findByEmail("a@l");
        verify(cr).findByEmail("b@b");
    }

@Test
void findAllTest (){
    Customer c1=new Customer();
    Customer c2=new Customer();
    Customer c3=new Customer();

    customersFound.add(c1);
    customersFound.add(c2);
    customersFound.add(c3);

    when (cr.findAll()).thenReturn(customersFound);
    List<CustomerDto> mockCustomers= cs.findAll();

    assertEquals(4,mockCustomers.size());
    verify(cr).findAll();
}
    @Test
    void updateCustomerTest() {
        Customer updatedCustomer= new Customer(1,"Ana","Lemos","AL","ugabugas","a@l",new LinkedList<>());

        //if the customer exists
        when(cr.findById(1)).thenReturn(Optional.of(updatedCustomer)); //guarantee that the method returns an existing customer
        when(cr.save(any(Customer.class))).thenReturn(updatedCustomer);
        cs.updateCustomer(1,customerDto);

        verify(cr).findById(1);
        verify(cr).save(any());

        //if the customer doesn't exist
        assertThrows(NoSuchElementException.class,()->
        cs.updateCustomer(5,customerDto));

        verify(cr).save(any(Customer.class));
        verify(cr,times(2)).findById(anyInt());
    }
    @Test
    void validEmailAddress() {
        Customer customer = new Customer();
        customer.setEmail("andre@email.com");

        Errors errors = new BeanPropertyBindingResult(customer, "customer");
        validator.validate(customer, errors);

        assertFalse(errors.hasErrors());
    }

    @Test
    void invalidEmailAddress() {
        Customer customer = new Customer();
        customer.setEmail("invalid-email");

        Errors errors = new BeanPropertyBindingResult(customer, "customer");
        validator.validate(customer, errors);

        assertTrue(errors.hasFieldErrors("email"));
        assertEquals("field.invalidFormat", errors.getFieldError("email").getCode());
    }
    @Test
    void emptyEmailAddress() {
        Customer customer = new Customer();
        customer.setEmail("");

        Errors errors = new BeanPropertyBindingResult(customer, "customer");
        validator.validate(customer, errors);

        assertTrue(errors.hasFieldErrors("email"));
        assertEquals("Email must not be empty", errors.getFieldError("email").getDefaultMessage());
    }

    @Test
    void nullEmailAddress() {
        Customer customer = new Customer();
        customer.setEmail(null);

        Errors errors = new BeanPropertyBindingResult(customer, "customer");
        validator.validate(customer, errors);

        assertTrue(errors.hasFieldErrors("email"));
        assertEquals("Email must not be empty", errors.getFieldError("email").getDefaultMessage());
    }
}
