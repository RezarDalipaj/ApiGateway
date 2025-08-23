package org.dalipaj.apigateway.auth.service.impl;

import lombok.RequiredArgsConstructor;
import org.dalipaj.apigateway.auth.service.IHashService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HashService implements IHashService {

    @Value("${app.salt}")
    private String salt;
    private final PasswordEncoder passwordEncoder;

    @Override
    public String salt(String raw) {
        return salt.concat(raw).concat(salt);
    }

    @Override
    public String encode(String raw) {
        var salted = salt(raw);
        return passwordEncoder.encode(salted);
    }
}
