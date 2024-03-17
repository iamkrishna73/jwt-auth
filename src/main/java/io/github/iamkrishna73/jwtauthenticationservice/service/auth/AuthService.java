package io.github.iamkrishna73.jwtauthenticationservice.service.auth;

import io.github.iamkrishna73.jwtauthenticationservice.entity.AppUser;
import io.github.iamkrishna73.jwtauthenticationservice.repository.AppUserRepository;
import io.github.iamkrishna73.jwtauthenticationservice.util.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class AuthService implements IAuthService{
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final AppUserRepository appUserRepository;
    public AuthService(AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, AppUserRepository appUserRepository) {
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.appUserRepository = appUserRepository;
    }


    @Override
    public String login(String username, String password) {
        var authToken = new UsernamePasswordAuthenticationToken(username, password);
        var authenticate = authenticationManager.authenticate(authToken);
        //Generate Token
        return JwtUtils.generateToken(((UserDetails)(authenticate.getPrincipal())).getUsername());
    }

    @Override
    public String signup(String name, String username, String password) {
        // Check whether user already exists or not
        boolean isUserExist = appUserRepository.existsByUsername(username);
        if(isUserExist) {
            throw new RuntimeException("User already exists");
        }
        // Encoder password
        var encoder = passwordEncoder.encode(password);
        //Authorities
        var authorties = new ArrayList<GrantedAuthority>();
        authorties.add(new SimpleGrantedAuthority("ROLE_USER"));

        //Create App User
        var appUser = AppUser.builder().name(name).username(username).password(password).authorities(authorties).build();

        //save user
        appUserRepository.save(appUser);

        //Generate Token
        return JwtUtils.generateToken(username);
    }

    @Override
    public String verifyToken(String token) {
        var usernameOptinal = JwtUtils.getUsernameFromToken(token);
        if(usernameOptinal.isPresent()){
           return usernameOptinal.get();
        }
        throw new RuntimeException("Token Invalid");
    }
}
