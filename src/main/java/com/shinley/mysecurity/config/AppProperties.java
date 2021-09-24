package com.shinley.mysecurity.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "imooc")
public class AppProperties {

    @Getter
    @Setter
    private Jwt jwt = new Jwt();

    @Setter
    @Getter
    public static class Jwt {
        private String header = "Authorization";
        private String prefix = "Bearer ";
        private Long accessTokenExpireTime = 60_000L;

        private Long refreshTokenExpireTime = 30 * 24 * 3600 * 1000L;
    }
}
