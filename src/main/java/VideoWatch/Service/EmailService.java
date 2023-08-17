package VideoWatch.Service;

import VideoWatch.Model.Email;
import VideoWatch.Repository.EmailRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService implements EmailServiceInterface{

    private JavaMailSender javaMailSender;
    private EmailRepository emailRepository;

    public EmailService(JavaMailSender javaMailSender, EmailRepository emailRepository) {
        this.javaMailSender = javaMailSender;
        this.emailRepository = emailRepository;
    }

    public void sendEmail(Email email) throws MessagingException{
        //every sent email is persisted
        Email persistedEmail= new Email();
        persistedEmail.setId(email.getId());
        persistedEmail.setSender(email.getSender());
        persistedEmail.setReceivers(email.getReceivers());
        persistedEmail.setSubject(email.getSubject());
        persistedEmail.setBody(email.getBody());

        emailRepository.save(persistedEmail);

        //API send email
        MimeMessage mimeMessage=javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper= new MimeMessageHelper(mimeMessage,"utf-8");
        mimeMessageHelper.setFrom(email.getSender());
        mimeMessageHelper.setSubject(email.getSubject());
        mimeMessageHelper.setText(email.getBody(),true);
        mimeMessageHelper.setTo(email.getReceivers().toArray(new String[email.getReceivers().size()]));

    javaMailSender.send(mimeMessage);
    }
}
