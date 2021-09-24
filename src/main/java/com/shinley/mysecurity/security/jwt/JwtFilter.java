package com.shinley.mysecurity.security.jwt;

import com.shinley.mysecurity.config.AppProperties;
import com.shinley.mysecurity.utils.CollectionUtil;
import com.shinley.mysecurity.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final AppProperties appProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (checkJwtToken(request)) {
            // Authorization: Bearer xxx
            validateToken(request).filter(claims -> claims.get("authorities") != null)
                    .ifPresentOrElse(claims -> {
                        // 有值
                        setupSpringAuthentication(claims);
                    }, () -> {
                        // 没有值
                        SecurityContextHolder.clearContext();
                    });

        }
        filterChain.doFilter(request, response);
    }

    private void setupSpringAuthentication(Claims claims) {
        val rawList = CollectionUtil.convertObjectToList(claims.get("authorities"));
        val authorities = rawList.stream()
                .map(String::valueOf)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        val authentication = new UsernamePasswordAuthenticationToken(claims.getSubject(), null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private Optional<Claims> validateToken(HttpServletRequest request) {
        String jwtToken = request.getHeader(appProperties.getJwt().getHeader()).replace(appProperties.getJwt().getPrefix(), "");
        try {
            return Optional.of(Jwts.parserBuilder().setSigningKey(JwtUtil.key).build().parseClaimsJws(jwtToken).getBody());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     *
     * @param request
     * @return
     */
    private boolean checkJwtToken(HttpServletRequest request) {
        String authenticationHeader = request.getHeader(appProperties.getJwt().getHeader());
        return authenticationHeader != null && authenticationHeader.startsWith(appProperties.getJwt().getPrefix());
    }
}
