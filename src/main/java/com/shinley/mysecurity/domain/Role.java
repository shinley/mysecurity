package com.shinley.mysecurity.domain;

import lombok.Builder;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

@Builder
@Entity
@Table(name = "mooc_roles")
public class Role implements GrantedAuthority {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "role_name", unique = true, nullable = false, length = 50)
    private String authority;

    @Override
    public String getAuthority() {
        return null;
    }
}
