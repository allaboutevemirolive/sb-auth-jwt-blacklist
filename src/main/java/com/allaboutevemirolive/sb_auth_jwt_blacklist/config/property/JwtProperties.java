package com.allaboutevemirolive.sb_auth_jwt_blacklist.config.property;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application-jwt.properties")
public class JwtProperties {

    @Value("${security.jwt.secret}")
    private String secret;

    @Value("${security.jwt.access.token.expiration}")
    private long accessTokenExpiration;

    @Value("${security.jwt.refresh.token.expiration}")
    private long refreshTokenExpiration;

    @Value("${security.jwt.issuer}")
    private String issuer;

    @Value("${security.jwt.audience}")
    private String audience;

    @Value("${security.jwt.token.prefix}")
    private String tokenPrefix;

    @Value("${security.jwt.header}")
    private String header;

    @Value("${security.jwt.refresh.rotation.enabled}")
    private boolean refreshRotationEnabled;

    @Value("${security.jwt.refresh.grace.period}")
    private long refreshGracePeriod;

    @Value("${security.jwt.allowed.clock.skew}")
    private long allowedClockSkew;

    @Value("${security.jwt.algorithm}")
    private String algorithm;

    @Value("${security.jwt.key.public}")
    private String publicKeyPath;

    @Value("${security.jwt.key.private}")
    private String privateKeyPath;

    @Value("${security.jwt.blacklist.source}")
    private String blacklistSource;

    @Value("${security.jwt.blacklist.expiration}")
    private long blacklistExpiration;

    @Value("${security.jwt.cookie.enabled}")
    private boolean cookieEnabled;

    @Value("${security.jwt.cookie.name}")
    private String cookieName;

    @Value("${security.jwt.compress}")
    private boolean compress;

    @Value("${security.jwt.encrypt}")
    private boolean encrypt;

    @Value("${security.jwt.custom.claims.enabled}")
    private boolean customClaimsEnabled;

    public String getSecret() {
        return secret;
    }

    public long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    public long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }

    public String getIssuer() {
        return issuer;
    }

    public String getAudience() {
        return audience;
    }

    public String getTokenPrefix() {
        return tokenPrefix;
    }

    public String getHeader() {
        return header;
    }

    public boolean isRefreshRotationEnabled() {
        return refreshRotationEnabled;
    }

    public long getRefreshGracePeriod() {
        return refreshGracePeriod;
    }

    public long getAllowedClockSkew() {
        return allowedClockSkew;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public String getPublicKeyPath() {
        return publicKeyPath;
    }

    public String getPrivateKeyPath() {
        return privateKeyPath;
    }

    public String getBlacklistSource() {
        return blacklistSource;
    }

    public long getBlacklistExpiration() {
        return blacklistExpiration;
    }

    public boolean isCookieEnabled() {
        return cookieEnabled;
    }

    public String getCookieName() {
        return cookieName;
    }

    public boolean isCompress() {
        return compress;
    }

    public boolean isEncrypt() {
        return encrypt;
    }

    public boolean isCustomClaimsEnabled() {
        return customClaimsEnabled;
    }
}
