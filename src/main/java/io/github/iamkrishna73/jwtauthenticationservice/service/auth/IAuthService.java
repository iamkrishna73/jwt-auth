package io.github.iamkrishna73.jwtauthenticationservice.service.auth;

public interface IAuthService  {
    String login(String username, String password);
    String signup(String name, String username, String password);
    String verifyToken(String token);
}
