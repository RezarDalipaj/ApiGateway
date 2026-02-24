package org.dalipaj.apigateway.common;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PreAuthorizeConstants {

    public static final String ADMIN = "hasAuthority('ROLE_ADMIN')";
    public static final String APPLICATION = "hasAuthority('ROLE_APPLICATION')";
}
