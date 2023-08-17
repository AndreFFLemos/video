package VideoWatch.Model;

import VideoWatch.DTO.CustomerDto;

public class UserLoginResponse {

    private String token;
    private CustomerDto customerDto;

    public UserLoginResponse() {
    }

    public UserLoginResponse(String token, CustomerDto customerDto) {
        this.token = token;
        this.customerDto = customerDto;
    }

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }

    public CustomerDto getCustomerDto() {
        return customerDto;
    }

    public void setCustomerDto(CustomerDto customerDto) {
        this.customerDto = customerDto;
    }
}
