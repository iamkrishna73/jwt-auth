package io.github.iamkrishna73.jwtauthenticationservice.controller;

import io.github.iamkrishna73.jwtauthenticationservice.dto.AuthRequestDto;
import io.github.iamkrishna73.jwtauthenticationservice.dto.AuthResponseDto;
import io.github.iamkrishna73.jwtauthenticationservice.enums.AuthStatus;
import io.github.iamkrishna73.jwtauthenticationservice.service.auth.IAuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final IAuthService authService;

    public AuthController(IAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody AuthRequestDto authRequestDto) {
        var jwtToken = authService.login(authRequestDto.username(), authRequestDto.password());
        var authResponseDto = new AuthResponseDto(jwtToken, AuthStatus.LOGIN_SUCCESS);

        return ResponseEntity.status(HttpStatus.OK).body(authResponseDto);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<AuthResponseDto> signup(@RequestBody AuthRequestDto authRequestDto) {
        try {
            var jwtToken = authService.signup(authRequestDto.name(), authRequestDto.username(), authRequestDto.password());
            var authResponseDto = new AuthResponseDto(jwtToken, AuthStatus.USER_CREATED_SUCCESSFULLY);

            return ResponseEntity.status(HttpStatus.OK).body(authResponseDto);
        } catch (Exception exception) {
            var authResponseDto = new AuthResponseDto(null, AuthStatus.USER_NOT_CREATED);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(authResponseDto);
        }
    }

    @PostMapping("/verify-token")
    public ResponseEntity<String> verifyToken(@RequestBody String token) {
       try {
           var username = authService.verifyToken(token.substring(7));
           return ResponseEntity.status(HttpStatus.OK).body(username);
       } catch (Exception exception){
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

       }
    }
}
