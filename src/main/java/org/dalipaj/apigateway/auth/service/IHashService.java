package org.dalipaj.apigateway.auth.service;

public interface IHashService {
    String salt(String raw);

    String encode(String raw);
}
