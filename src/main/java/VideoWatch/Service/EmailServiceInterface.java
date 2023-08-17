package VideoWatch.Service;

import VideoWatch.Model.Email;
import jakarta.mail.MessagingException;

public interface EmailServiceInterface {
    void sendEmail(Email email) throws MessagingException;
}
