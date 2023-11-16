package com.example.userservice.vo;


import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RequestUser {

    @NotNull(message = "Email cannot be null")
    @Size(min = 2, message = "Email not be less than two char")
    @Email
    private String email;

    @NotNull(message = "Name cannot be null")
    @Size(min = 2, message = "Name not be less than two char")
    private String name;

    @NotNull(message = "Password cannot be null")
    @Size(min = 2, message = "Password must be less than 8 char")
    private String pwd;
}
