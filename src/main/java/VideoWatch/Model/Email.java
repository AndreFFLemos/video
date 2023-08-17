package VideoWatch.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name="Emails")
public class Email {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;
    @Column(name="subject",nullable = false)
    private String subject;
    @Column(name="body",nullable = false)
    private String body;
    @Column(name="sender",nullable = false)
    private String sender;
    @Column(name="receivers",nullable = false)
    private List<String> receivers;

}