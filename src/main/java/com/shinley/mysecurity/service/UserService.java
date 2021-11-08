package com.shinley.mysecurity.service;

import com.shinley.mysecurity.config.Constants;
import com.shinley.mysecurity.domain.Auth;
import com.shinley.mysecurity.domain.User;
import com.shinley.mysecurity.repository.RoleRepo;
import com.shinley.mysecurity.repository.UserRepo;
import com.shinley.mysecurity.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public void register(User user) {
        roleRepo.findOptionalByAuthority(Constants.ROLE_USER)
                .map(role -> {
                    val userToSave =user.withAuthorities(Set.of(role))
                            .withPassword(passwordEncoder.encode(user.getPassword()));
                    return userRepo.save(userToSave);
                }).orElseThrow();
    }


    public Auth login(String username, String passowrd) {
        return userRepo.findOptionalByUsername(username)
                .filter(user -> passwordEncoder.matches(passowrd, user.getPassword()))
                .map(user -> new Auth(
                        jwtUtil.createAccessToken(user),
                        jwtUtil.createRefreshToken(user)
                )).orElseThrow(() -> new BadCredentialsException("用户名或密码错误"));
    }

    public boolean isUsernameExisted(String username) {
        return userRepo.countByUsername(username) > 0;
    }

    public boolean isEmailExisted(String email) {
        return userRepo.countByEmail(email) > 0;
    }

    public boolean isMobileExisted(String mobile) {
        return userRepo.countByUsername(mobile) > 0;
    }
}
