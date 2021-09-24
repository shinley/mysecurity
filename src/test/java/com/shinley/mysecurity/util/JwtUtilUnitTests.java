package com.shinley.mysecurity.util;

import com.shinley.mysecurity.config.AppProperties;
import com.shinley.mysecurity.domain.Role;
import com.shinley.mysecurity.domain.User;
import com.shinley.mysecurity.utils.JwtUtil;
import io.jsonwebtoken.Jwts;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class JwtUtilUnitTests {


    private JwtUtil jwtUtil;

    @BeforeEach
    public void setup() {
        jwtUtil = new JwtUtil(new AppProperties());
    }

    @Test
    public void givenUserDetails_thenCreateTokenSuccess() {
        val username = "user";
        val authorities = Set.of(
                Role.builder().authority("ROLE_USER").build(),
                Role.builder().authority("ROLE_ADMIN").build()
                );
        val user = User.builder().username(username).authorities(authorities).build();

        val token = jwtUtil.createAccessToken(user);

        // 解析
        val parsedClaims = Jwts.parserBuilder()
                .setSigningKey(jwtUtil.key)
                .build().parseClaimsJws(token)
                .getBody();

        assertEquals(username, parsedClaims.getSubject());

    }
}
