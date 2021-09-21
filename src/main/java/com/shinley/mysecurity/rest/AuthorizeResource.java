package com.shinley.mysecurity.rest;

import com.shinley.mysecurity.domain.dto.UserDto;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authorize")
public class AuthorizeResource {
    @PostMapping("/register")
    public UserDto regiseter(@Validated @RequestBody UserDto userDto) {
        return userDto;
    }
}
