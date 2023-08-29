package VideoWatch.Repository;

import VideoWatch.Model.BlackListedTokens;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlackListTokenRepository extends JpaRepository<BlackListedTokens, Long> {

        Optional<BlackListedTokens> findByToken(String token);

    }
