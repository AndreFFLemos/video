package VideoWatch.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BlackListedTokens {

    /*The blacklisted tokens should be stored in the database for
    Persistence just in case of server restarts or crashes or scalability, when there is multiple instances running,
    they can all reference a central database to check for blacklisted tokens but these have the caveat of storage overhead,
    especially if there are a lot of logouts.
     An alternative approach is to use short-lived JWTs and force token refreshes.
     If a user logs out, just blacklist or invalidate their refresh token,
     which is less frequently than the JWT.

     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    private LocalDateTime expiryDate; // to remove expired tokens

    public BlackListedTokens(String token, LocalDateTime expiryDate) {
        this.token = token;
        this.expiryDate = expiryDate;
    }
}