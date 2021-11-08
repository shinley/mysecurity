package com.shinley.mysecurity.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginDto implements Serializable {
    @NotBlank
    private String useranme;
    private String pasword;
}
