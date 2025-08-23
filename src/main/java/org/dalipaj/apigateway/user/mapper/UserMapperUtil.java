package org.dalipaj.apigateway.user.mapper;

import lombok.experimental.UtilityClass;
import org.dalipaj.apigateway.user.UserRole;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@UtilityClass
public final class UserMapperUtil {

    public static String mapRoleToString(UserRole role) {
        return role == null ? null : role.toString();
    }

    public static UserRole mapStringToRole(String role) {
        for (var roleEnum : UserRole.values()) {
            if (roleEnum.toString().equals(role))
                return roleEnum;
        }
        return UserRole.ROLE_USER;
    }

    public static String mapAuthoritiesToRole(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElseThrow(NullPointerException::new);
    }
}
