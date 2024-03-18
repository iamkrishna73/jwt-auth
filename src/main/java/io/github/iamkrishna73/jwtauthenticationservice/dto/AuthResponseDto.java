package io.github.iamkrishna73.jwtauthenticationservice.dto;

import io.github.iamkrishna73.jwtauthenticationservice.enums.AuthStatus;

public record AuthResponseDto (String token, AuthStatus authStatus){

}
