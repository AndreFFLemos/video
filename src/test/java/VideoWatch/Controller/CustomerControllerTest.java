package VideoWatch.Controller;

import VideoWatch.VideoApplication;
import VideoWatch.DTO.CustomerDto;
import VideoWatch.Model.Customer;
import VideoWatch.Model.Movie;
import VideoWatch.Service.CustomerServiceInterface;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.*;


@SpringBootTest(classes = VideoApplication.class)
public class CustomerControllerTest {

    private Movie movie;
    private MockMvc mockMvc; // MockMvc class simulates http requests to the controllers in a test environment without using tomcat server
    @Mock
    private CustomerServiceInterface customerServiceInterface;
    @InjectMocks
    private CustomerController customerController;

    private Customer customer;
    private CustomerDto customerDto;
    private List<CustomerDto> customerDtos;
    private final ObjectMapper objectMapper= new ObjectMapper();//the objectmapper converts the Dto instance to a json format

    @BeforeEach
    public void setup(){
        movie= new Movie();
        customerDtos=new LinkedList<>();
        customer= new Customer(1,"A","L","AL",null,"a@l", Collections.singletonList(movie));
        customerDto=new CustomerDto("A","L","AL","a@l");
        customerDtos.add(customerDto);
        // the following creates the MockMvc instance
        mockMvc=MockMvcBuilders.standaloneSetup(customerController).build();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void updateCustomerTest() throws Exception {
        customerDto.setFirstName("B");
        customerDto.setLastName("M");

        String requestBody= objectMapper.writeValueAsString(customerDto);

        var requestBuilder= MockMvcRequestBuilders.put("/api/customer/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);

        doNothing().when(customerServiceInterface).updateCustomer(1,customerDto);

        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(customerServiceInterface).updateCustomer(1,customerDto);
    }
    @Test
    public void deleteCustomerTest() throws Exception {
        String requestBody= objectMapper.writeValueAsString(customerDto);

        var requestBuilder= MockMvcRequestBuilders.delete("/api/customer/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);

        //because the method deleteCustomer returns a void, then we tell mockito to do nothing when the method is invoked
        doNothing().when(customerServiceInterface).deleteCustomer(1);

        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(customerServiceInterface).deleteCustomer(1);
    }

    @Test
    public void findCustomerByIdTest() throws Exception {

        var requestBuilder= MockMvcRequestBuilders.get("/api/customer/byid?id=1");

        when(customerServiceInterface.findCustomerById(1)).thenReturn(customerDto);

        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(customerServiceInterface).findCustomerById(1);
    }

    @Test
    public void findCustomerByFirstNameTest() throws Exception {
        var requestBuilder= MockMvcRequestBuilders.get("/api/customer/byfirstname?firstname=A");

        when(customerServiceInterface.findCustomerByFirstName("A")).thenReturn(customerDtos);

        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(customerServiceInterface).findCustomerByFirstName("A");

    }
    @Test
    public void findCustomerByLastNameTest() throws Exception {
        var requestBuilder= MockMvcRequestBuilders.get("/api/customer/bylastname?lastName=L");

        when(customerServiceInterface.findCustomerByLastName("L")).thenReturn(customerDtos);

        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(customerServiceInterface).findCustomerByLastName("L");
    }

    @Test
    public void findAllCustomersTest() throws Exception {

        String requestBody=objectMapper.writeValueAsString(customerDtos);

        //instance that creates the request
        //var is a local variable
        var requestBuilder= MockMvcRequestBuilders.get("/api/customer/all")
                        .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody);

        when(customerServiceInterface.findAll()).thenReturn(customerDtos);

        //the perform method simulates a request to the endpoint, like sending in Postman
        mockMvc.perform(requestBuilder)
                //asserts
                .andExpect(MockMvcResultMatchers.status().isOk())
                //and compare the json in the requestbody with the expected json representation
                .andExpect(MockMvcResultMatchers.content().json(requestBody));
    }



}
