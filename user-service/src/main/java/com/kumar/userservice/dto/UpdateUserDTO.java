package com.kumar.userservice.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDTO {

    @Size(
            min = 3,
            max = 50,
            message = "User name must be between 3 and 50 characters"
    )
    private String userName;

    @Email(message = "Invalid email format")
    private String email;

    @Size(
            min = 8,
            max = 20,
            message = "Password must be between 8 and 20 characters"
    )
    private String password;

    @Pattern(
            regexp = "^[6-9]\\d{9}$",
            message = "Mobile number must be a valid 10-digit Indian mobile number"
    )
    private String mobileNumber;

    @Min(
            value = 18,
            message = "User must be at least 18 years old"
    )
    @Max(
            value = 100,
            message = "Age must not exceed 100"
    )
    private Integer age;

    public boolean isEmpty() {
        return userName == null
                && email == null
                && password == null
                && mobileNumber == null
                && age == null;
    }
}
