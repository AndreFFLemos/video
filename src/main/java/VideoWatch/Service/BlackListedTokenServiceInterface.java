package VideoWatch.Service;

public interface BlackListedTokenServiceInterface {
    void addTokenToBlacklist(String token);
    boolean isTokenBlacklisted(String token);

}
