package com.kumar.userservice.service;

import com.kumar.userservice.dto.UpdateUserDTO;
import com.kumar.userservice.dto.UserRequestDTO;
import com.kumar.userservice.dto.UserResponseDTO;
import org.springframework.data.domain.Page;


public interface UserService {

    // Create User
    UserResponseDTO createUser(UserRequestDTO requestDTO);

    // Get User By Id
    UserResponseDTO getUserById(Long userId);

    // Get All Users (Pagination)
    Page<UserResponseDTO> getAllUsers(
            int page,
            int size,
            String sortBy,
            String direction
    );

    // Update User
    UserResponseDTO updateUser(
            Long userId,
            UpdateUserDTO updateUserDTO
    );

    // Delete User
    void deleteUser(Long userId);

}


