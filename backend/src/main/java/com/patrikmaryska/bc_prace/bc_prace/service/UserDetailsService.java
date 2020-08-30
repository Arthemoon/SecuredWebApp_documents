package com.patrikmaryska.bc_prace.bc_prace.service;

import com.patrikmaryska.bc_prace.bc_prace.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    @Autowired
    private  UserService userService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private  HttpServletRequest request;

    @Override
    public UserDetails loadUserByUsername(String username) {

        String ip = getClientIP();
        if (loginService.isBlocked(ip)) {
            throw new RuntimeException("You are blocked.");
        }

         Optional<com.patrikmaryska.bc_prace.bc_prace.model.User> userEntity = userService.getUserByEmail(username);

        if (userEntity.isPresent()) {
            final com.patrikmaryska.bc_prace.bc_prace.model.User appUser = userEntity.get();

            if(BCrypt.checkpw(request.getParameter("password"), appUser.getPassword())){
                loginService.loginSucceeded(username, getClientIP());
            } else if(request.getParameter("password") == null) {
                // REFRESH TOKEN
            } else {
                loginService.loginFailed(username, getClientIP());
            }

            return new User(appUser.getEmail(),
                    appUser.getPassword(),
                    getAuthority(appUser.getId()));
        }

        throw new BadCredentialsException("Bad credentials");
        }

    private List<SimpleGrantedAuthority> getAuthority(long id) {

        List<Role> roles = userService.getUsersRoles(id);
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        roles.stream()
                .forEach(p -> {
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + p));
                });

        return authorities;
    }

    private String getClientIP() {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null){
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
