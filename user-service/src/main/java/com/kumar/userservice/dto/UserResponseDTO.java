package com.kumar.userservice.dto;

import com.kumar.userservice.entity.enums.CreatedByType;
import com.kumar.userservice.entity.enums.UserRole;
import com.kumar.userservice.entity.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

    private Long userId;

    private String userName;

    private String email;

    private String mobileNumber;

    private Integer age;

    private UserRole role;

    private UserStatus status;

    private LocalDateTime createdAt;

    private CreatedByType createdBy;

    private LocalDateTime updatedAt;

    private String updatedBy;
}