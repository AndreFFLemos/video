package VideoWatch.Controller;

import VideoWatch.Model.Email;
import org.springframework.web.bind.annotation.RequestBody;

public interface EmailControllerInterface {

    String sendEmail(@RequestBody Email email);
}
