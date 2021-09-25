package com.shinley.mysecurity.service;

import com.shinley.mysecurity.domain.Auth;
import com.shinley.mysecurity.repository.UserRepo;
import com.shinley.mysecurity.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;


    public Auth login(String username, String passowrd) {
        return userRepo.findOptionalByUsername(username)
                .filter(user -> passwordEncoder.matches(passowrd, user.getPassword()))
                .map(user -> new Auth(
                        jwtUtil.createAccessToken(user),
                        jwtUtil.createRefreshToken(user)
                )).orElseThrow(() -> new BadCredentialsException("用户名或密码错误"));
    }
}
