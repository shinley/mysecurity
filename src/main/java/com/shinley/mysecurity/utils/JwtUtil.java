package com.shinley.mysecurity.utils;

import com.shinley.mysecurity.config.AppProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class JwtUtil {

    public static  final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    public static  final Key refreshKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    private final AppProperties appProperties;

    public String createAccessToken(UserDetails userDetails) {
        return createJwtToken(userDetails,appProperties.getJwt().getAccessTokenExpireTime(), key);
    }

    public String createRefreshToken(UserDetails userDetails) {
        return createJwtToken(userDetails, appProperties.getJwt().getRefreshTokenExpireTime(), refreshKey);
    }

    /**
     * 使用 refreshToken 创建 accessToken
     * @param token
     * @return
     */
    public String createAccessTokenWithRefreshTokien(String token) {
        return parseClaims(token, refreshKey).map(claims -> Jwts.builder()
                .setClaims(claims).setExpiration(new Date(System.currentTimeMillis() + appProperties.getJwt().getAccessTokenExpireTime()))
                .setIssuedAt(new Date())
                .signWith(key, SignatureAlgorithm.HS512).compact())
                .orElseThrow(() -> new AccessDeniedException("访问被拒绝"));
    }

    private Optional<Claims> parseClaims(String token, Key key) {
        try {
            val claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJwt(token).getBody();
            return Optional.of(claims);
        }catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * 验证token, 不考虑过期时间
     * @param token
     * @return
     */
    public boolean validateAccessTokenWithoutExpiration(String token) {
        return validateToken(token, key, false);
    }

    /**
     * 验证accessToken, 考虑过期时间; 如果过期了，认为不合法
     * @param token
     * @return
     */
    public boolean validateAccessToken(String token) {
        return validateToken(token, key, true);
    }

    /**
     * 验证refreshToke, 考虑过期时间， 如果过期了， 认为不合法
     * @param token
     * @return
     */
    public boolean validateRefreshToken(String token) {
        return validateToken(token, refreshKey, true);
    }

    /**
     * 验证token
     * @param token
     * @param key
     * @param isExpiredInvalid true过期表示非法
     * @return
     */
    public boolean validateToken(String token, Key key, boolean isExpiredInvalid) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parse(token);
            return true;
        }catch (Exception e) {
            if (e instanceof ExpiredJwtException) {
                return !isExpiredInvalid;
            }
            return false;
        }
    }

    public String createJwtToken(UserDetails userDetails, long timeToExpire, Key key) {
        val now = System.currentTimeMillis();
        return Jwts.builder()
                .setId("mooc")
                .claim("authorities", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + 60_000))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

    }
}
