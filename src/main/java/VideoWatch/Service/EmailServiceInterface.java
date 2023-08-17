package VideoWatch.Service;

import VideoWatch.Model.Email;
import VideoWatch.Model.UserRegistrationRequest;
import jakarta.mail.MessagingException;

public interface EmailServiceInterface {
    void sendEmail(Email email) throws MessagingException;
    public void sendWelcomingEmail(UserRegistrationRequest registrationRequest);
}
