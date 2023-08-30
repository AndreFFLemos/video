package VideoWatch.Service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordService{

private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

//when a user registers the api uses this
public String hashPassword(String plainPassword) {
        return passwordEncoder.encode(plainPassword);
        }

        //when a user logs in the api uses this to compare the introduced password with the persisted hash
public boolean checkPassword(String plainPassword, String hashedPassword) {
        return passwordEncoder.matches(plainPassword, hashedPassword);
        }
}