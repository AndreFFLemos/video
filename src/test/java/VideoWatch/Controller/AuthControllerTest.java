package VideoWatch.Controller;

import VideoWatch.DTO.CustomerDto;
import VideoWatch.Model.Customer;
import VideoWatch.Model.Movie;
import VideoWatch.Model.UserRegistrationRequest;
import VideoWatch.Service.CustomerServiceInterface;
import VideoWatch.Service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {
    @InjectMocks
    private AuthController authController;
    @Mock
    private CustomerServiceInterface customerServiceInterface;
    private UserRegistrationRequest userRegistrationRequest;
    @Mock
    private EmailService emailService;

    private Movie movie;
    private MockMvc mockMvc; // MockMvc class simulates http requests to the controllers in a test environment without using tomcat server
    private Customer customer;
    private CustomerDto customerDto;
    private List<CustomerDto> customerDtos;
    private final ObjectMapper objectMapper= new ObjectMapper();//the objectmapper converts the Dto instance to a json format


    @BeforeEach
    void setup() {
        movie = new Movie();
        userRegistrationRequest = new UserRegistrationRequest();
        userRegistrationRequest.setName("Duarte");
        userRegistrationRequest.setEmail("duarte@gmail.com");
        userRegistrationRequest.setPassword("Soubenfiquista");
        customerDtos = new LinkedList<>();
        customer = new Customer(1, "A", "L", "AL", null,  "a@l", Collections.singletonList(movie));
        customerDto = new CustomerDto("Duarte", null, null, "duarte@gmail.com");
        customerDtos.add(customerDto);
        // the following creates the MockMvc instance
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void createCustomerTest() throws Exception {
        //the object mapper is converting the customerDto instance in to a json format and saving it in the request body
        String requestBody= objectMapper.writeValueAsString(userRegistrationRequest);
        System.out.println(requestBody);
        //i'm telling the mockmvc to build a post request with the content type json and the content is the instance converted
        var requestBuilder= MockMvcRequestBuilders.post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);

        when(customerServiceInterface.createCustomer(userRegistrationRequest)).thenReturn(customerDto);

        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(customerServiceInterface).createCustomer(userRegistrationRequest);
    }
}
