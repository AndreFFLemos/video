package VideoWatch.Service;

import VideoWatch.Model.BlackListedTokens;
import VideoWatch.Repository.BlackListTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BlackListedTokenService implements BlackListedTokenServiceInterface {

        private BlackListTokenRepository blackListTokenRepository;

        @Autowired
        public BlackListedTokenService(BlackListTokenRepository blackListTokenRepository) {
            this.blackListTokenRepository = blackListTokenRepository;
        }

        public void addTokenToBlacklist(String token) {
            if (!isTokenBlacklisted(token)) {
                BlackListedTokens tokenBlacklist = new BlackListedTokens(token, LocalDateTime.now().plusHours(24)); // assuming the token is valid for 24 hours
                blackListTokenRepository.save(tokenBlacklist);
            }
        }

        public boolean isTokenBlacklisted(String token) {
            return blackListTokenRepository.findByToken(token).isPresent();
        }

    }

