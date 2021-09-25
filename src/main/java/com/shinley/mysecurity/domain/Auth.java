package com.shinley.mysecurity.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Auth implements Serializable {
    private String accessToken;

    private String refreshToken;
}
