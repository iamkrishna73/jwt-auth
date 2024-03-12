package io.github.iamkrishna73.jwtauthenticationservice.configuration;

import io.github.iamkrishna73.jwtauthenticationservice.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;

    public JWTAuthenticationFilter(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Fetch token from request
        var jwtTokenOptional = getTokenFromRequest(request);
        // Validate jwt token -> JWT utils
        jwtTokenOptional.ifPresent(jwtToken -> {
            if(JwtUtils.validateToken(jwtToken)) {
                //Get username from jwt token
                var usernameOptional = JwtUtils.getUsernameFromToken(jwtToken);

                usernameOptional.ifPresent(username -> {
                    //Fetch the details with the help of username
                    var userDetails = userDetailsService.loadUserByUsername(username);

                    // Create Authentication token
                    var authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    //Set authentication token to security Context
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                });
            }
        });
        //Pass request an response to next filter
        filterChain.doFilter(request, response);
        
    }

    private Optional<String> getTokenFromRequest(HttpServletRequest request) {
        //Extract authentication header
        var authHearder = request.getHeader(HttpHeaders.AUTHORIZATION);

        //Bearer <JWT TOKEN>
        if(StringUtils.hasText(authHearder) && authHearder.startsWith("Bearer ")) {
            return Optional.of(authHearder.substring(7));
        }
        return Optional.empty();
    }
}
