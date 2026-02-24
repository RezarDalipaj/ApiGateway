package org.dalipaj.apigateway.upstream.service;

import jakarta.servlet.http.HttpServletRequest;
import org.dalipaj.apigateway.auth.UnAuthorizedException;
import org.dalipaj.apigateway.upstream.data.service.ServiceDto;
import org.dalipaj.apigateway.upstream.data.service.ServiceEntity;
import org.springframework.transaction.annotation.Transactional;

public interface IUpstreamTransactionalService {

    @Transactional
    Long saveEntity(ServiceDto serviceDto, HttpServletRequest request) throws UnAuthorizedException;

    ServiceEntity findById(Long id);
}
