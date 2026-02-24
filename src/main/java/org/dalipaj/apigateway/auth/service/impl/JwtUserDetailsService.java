package org.dalipaj.apigateway.auth.service.impl;

import org.dalipaj.apigateway.application.IApplicationService;
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

    private final IApplicationService applicationService;

    @Override
    public UserDetails loadUserByUsername(String appName) throws UsernameNotFoundException {
        try {
            var appDto = applicationService.getByName(appName);

            Collection<SimpleGrantedAuthority> authorityCollection = new ArrayList<>();
            authorityCollection.add(new SimpleGrantedAuthority(appDto.getRole()));
            return new User(appDto.getName(), appDto.getPassword(), authorityCollection);
        } catch (NullPointerException e) {
            throw new UsernameNotFoundException("Bad credentials");
        }
    }
}
