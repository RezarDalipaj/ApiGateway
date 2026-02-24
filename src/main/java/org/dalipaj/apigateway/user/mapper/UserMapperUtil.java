package org.dalipaj.apigateway.user.mapper;

import lombok.experimental.UtilityClass;
import org.dalipaj.apigateway.user.data.ScopeEntity;
import org.dalipaj.apigateway.user.data.UserRole;
import org.mapstruct.Named;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class UserMapperUtil {

    public static final String TO_DTO_SCOPES = "toDtoScopes";

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
                .orElseThrow(() -> new NullPointerException("There is no role assigned to the user"));
    }

    @Named(TO_DTO_SCOPES)
    public static Set<String> toDtoScopes(Set<ScopeEntity> scopes) {
        return scopes.stream()
                .map(ScopeEntity::getName)
                .collect(Collectors.toSet());
    }
}
