package VideoWatch.Model;

import jakarta.validation.constraints.Size;

import java.util.Objects;

public class UserRegistrationRequest {
    private String email;
    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters.")
    private String password;
    private String passwordConfirm;
    private String firstName;

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRegistrationRequest that = (UserRegistrationRequest) o;
        return Objects.equals(email, that.email) && Objects.equals(password, that.password) && Objects.equals(firstName, that.firstName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, password, firstName);
    }
}
