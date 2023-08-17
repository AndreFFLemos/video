package VideoWatch.Validation;

import VideoWatch.DTO.CustomerDto;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomerDtoValidator implements Validator{

    private static final String EMAIL_PATTERN = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final Pattern EMAIL_REGEX = Pattern.compile(EMAIL_PATTERN); //this compiles the email pattern in an object so we can use Pattern methods
    @Override
    public boolean supports(Class<?> clazz) {
        return CustomerDto.class.isAssignableFrom(clazz); // this ensures that the validator only applies to objects from the CustomerDto class
    }

    @Override
    public void validate(Object target, Errors errors) {
        CustomerDto customerDto = (CustomerDto) target;

        // Check if the field has nothing or whitespace
        if (customerDto.getEmail() == null || customerDto.getEmail().isEmpty()) {
            errors.rejectValue("email", "field.required", "Email must not be empty");
        } else { // if it has something then we check the object to the accepted email pattern
            Matcher matcher = EMAIL_REGEX.matcher(customerDto.getEmail());
            if (!matcher.matches()) {
                errors.rejectValue("email", "field.invalidFormat", "Invalid email format");
            }
        }
    }
}
