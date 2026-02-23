package org.dalipaj.apigateway.auth.service;

import java.security.NoSuchAlgorithmException;

public interface IHashService {

    String salt(String raw);

    String hash(String raw);

    String sha256(String raw) throws NoSuchAlgorithmException;
}
