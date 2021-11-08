package com.shinley.mysecurity.domain.dto;

import com.shinley.mysecurity.config.Constants;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
public class UserDto implements Serializable {

    @NonNull
    @NotBlank
    @Size(min =4, max= 40, message="用户名长度必须在4到50个字符之间")
    private String username;
    @Pattern(regexp = Constants.PATTERN_MOBILE)
    @NonNull
    private String mobile;
    @NonNull
    @NotBlank
    @Size(min = 8, max = 20, message = "密码长度必须在4到50个字符之间")
    private String password;
    @NonNull
    @NotBlank
    @Size(min = 8, max = 20, message = "密码长度必须在4到50个字符之间")
    private String matchPassword;
    @Email
    private String email;
    @NonNull
    @NotBlank
    @Size(min =4, max= 40, message="姓名长度必须在4到50个字符之间")
    private String name;
}
