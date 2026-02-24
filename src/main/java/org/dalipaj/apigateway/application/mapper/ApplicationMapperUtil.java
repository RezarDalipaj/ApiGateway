package org.dalipaj.apigateway.application.mapper;

import lombok.experimental.UtilityClass;
import org.dalipaj.apigateway.application.data.ApplicationRole;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@UtilityClass
public final class ApplicationMapperUtil {

    public static String mapRoleToString(ApplicationRole role) {
        return role == null ? null : role.toString();
    }

    public static ApplicationRole mapStringToRole(String role) {
        for (var roleEnum : ApplicationRole.values()) {
            if (roleEnum.toString().equals(role))
                return roleEnum;
        }
        return ApplicationRole.ROLE_APPLICATION;
    }

    public static String mapAuthoritiesToRole(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElseThrow(NullPointerException::new);
    }
}
