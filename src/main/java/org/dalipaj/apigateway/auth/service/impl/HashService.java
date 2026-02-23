package org.dalipaj.apigateway.auth.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.dalipaj.apigateway.auth.service.IHashService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

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
    public String hash(String raw) {
        var salted = salt(raw);
        return passwordEncoder.encode(salted);
    }

    @Override
    public String sha256(String raw) throws NoSuchAlgorithmException {
        if (Strings.isBlank(raw))
            return raw;

        var salted = salt(raw);
        var digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(salted.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(hash);
    }
}
