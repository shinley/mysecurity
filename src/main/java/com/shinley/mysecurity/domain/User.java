package com.shinley.mysecurity.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class User implements Serializable {
    private String username;
    private String password;
    private String email;
    private String name;
}
