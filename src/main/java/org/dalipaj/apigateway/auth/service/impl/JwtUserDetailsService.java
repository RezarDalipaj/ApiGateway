package org.dalipaj.apigateway.auth.service.impl;

import org.dalipaj.apigateway.user.IUserService;
import org.dalipaj.apigateway.auth.service.IJwtUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements IJwtUserDetailsService {

    private final IUserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            var userDto = userService.getByName(username);

            Collection<SimpleGrantedAuthority> authorityCollection = new ArrayList<>();
            authorityCollection.add(new SimpleGrantedAuthority(userDto.getRole()));
            return new User(userDto.getUsername(), userDto.getPassword(), authorityCollection);
        } catch (NullPointerException e) {
            throw new UsernameNotFoundException("Bad credentials");
        }
    }
}
