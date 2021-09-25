package com.shinley.mysecurity.rest;

import com.shinley.mysecurity.domain.Auth;
import com.shinley.mysecurity.domain.User;
import com.shinley.mysecurity.domain.dto.LoginDto;
import com.shinley.mysecurity.domain.dto.UserDto;
import com.shinley.mysecurity.exception.DuplicateProblem;
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
    public void regiseter(@Validated @RequestBody UserDto userDto) {
        // TODO 检查 username, email, mobile 都 是唯 一的， 所以要查询数据库确保唯一
        // TODO 我们需要userDto转换成User, 我们给一个默认角色， 然后保存
        if (userService.isUsernameExisted(userDto.getUsername())) {
            throw new DuplicateProblem("用户名重复");
        }
        if(userService.isEmailExisted(userDto.getEmail())) {
            throw new DuplicateProblem("电子邮件地址重复");
        }
        if (userService.isMobileExisted(userDto.getMobile())) {
            throw new DuplicateProblem("手机号重复");
        }
        val user = User.builder()
                .username(userDto.getUsername())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .mobile(userDto.getMobile())
                .password(userDto.getPassword())
                .build();
        userService.register(user);
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
