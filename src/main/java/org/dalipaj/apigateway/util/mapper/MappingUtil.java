package org.dalipaj.apigateway.util.mapper;

import org.dalipaj.apigateway.model.enumeration.Role;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@UtilityClass
public final class MappingUtil {

    public static String mapRoleToString(Role role) {
        return role == null ? null : role.toString();
    }

    public static Role mapStringToRole(String role) {
        for (var roleEnum : Role.values()) {
            if (roleEnum.toString().equals(role))
                return roleEnum;
        }
        return Role.USER;
    }

    public static String mapAuthoritiesToRole(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElseThrow(NullPointerException::new);
    }
}
