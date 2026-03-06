package com.ciao.clinica.backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

    private int maxLoginAttempts;

    private int accessTokenMinutes;
    private int refreshTokenDays;
    private int absoluteSessionDays;

}