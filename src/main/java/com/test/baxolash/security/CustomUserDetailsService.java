package com.test.baxolash.security;

import com.test.baxolash.entity.User;
import com.test.baxolash.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // username приходит как логин или email (см. AuthController)
        User user = userRepository.findByLoginAndDeletedAtIsNull(username)
                .or(() -> userRepository.findByEmailAndDeletedAtIsNull(username))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String roleName = "ROLE_" + user.getRole().name();

        return new org.springframework.security.core.userdetails.User(
                user.getLogin(),
                user.getPasswordHash(),
                user.getActive(),
                true,
                true,
                true,
                Collections.singletonList(new SimpleGrantedAuthority(roleName))
        );
    }
}

