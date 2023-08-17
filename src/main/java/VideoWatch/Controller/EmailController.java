package VideoWatch.Controller;

import VideoWatch.Model.Email;
import VideoWatch.Service.EmailServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/api")
public class EmailController implements EmailControllerInterface {
    private EmailServiceInterface emailServiceInterface;

    @Autowired
    public EmailController(EmailServiceInterface emailServiceInterface) {
        this.emailServiceInterface = emailServiceInterface;
    }

    @Override
    @PostMapping(value="/email")
    public String sendEmail(@RequestBody Email email){
        try {
            emailServiceInterface.sendEmail(email);
            return "it sended";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }
    }
}
