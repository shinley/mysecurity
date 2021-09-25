package com.shinley.mysecurity.rest;

import com.shinley.mysecurity.domain.Auth;
import com.shinley.mysecurity.domain.dto.LoginDto;
import com.shinley.mysecurity.domain.dto.UserDto;
import com.shinley.mysecurity.service.UserService;
import com.shinley.mysecurity.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/authorize")
public class AuthorizeResource {
    private final UserService userService;
    private final JwtUtil jwtUtil;


    @PostMapping("/register")
    public UserDto regiseter(@Validated @RequestBody UserDto userDto) {
        return userDto;
    }

    @PostMapping("/token")
    public Auth login(@RequestBody LoginDto loginDto) {
        return userService.login(loginDto.getUseranme(), loginDto.getPasword());
    }

    @PostMapping("/token/refresh")
    public Auth refreshToken(@RequestHeader(name = "Authorization")String authorization,
                             @RequestParam String refreshToken) throws AccessDeniedException {
        val PREFIX = "Bearer";
        val accessToken = authorization.replace(PREFIX, "");
        if (jwtUtil.validateRefreshToken(refreshToken) && jwtUtil.validateAccessTokenWithoutExpiration(accessToken)) {
            return new Auth(jwtUtil.createAccessTokenWithRefreshTokien(refreshToken), refreshToken);
        }
        throw new AccessDeniedException("访问被拒绝");
    }

}
