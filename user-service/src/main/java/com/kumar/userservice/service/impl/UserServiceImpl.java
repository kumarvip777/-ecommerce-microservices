package com.kumar.userservice.service.impl;

import com.kumar.userservice.dto.UpdateUserDTO;
import com.kumar.userservice.dto.UserRequestDTO;
import com.kumar.userservice.dto.UserResponseDTO;
import com.kumar.userservice.entity.User;
import com.kumar.userservice.entity.enums.UserRole;
import com.kumar.userservice.entity.enums.UserStatus;
import com.kumar.userservice.exception.user.NoUsersFoundException;
import com.kumar.userservice.exception.user.UserAlreadyExistsException;
import com.kumar.userservice.exception.user.UserNotFoundException;
import com.kumar.userservice.repo.UserRepository;
import com.kumar.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;


    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    @Transactional
    public UserResponseDTO createUser(@Valid UserRequestDTO requestDTO) {

        logger.info("Starting user registration for email: {}",
                requestDTO.getEmail());

        String email = requestDTO.getEmail().trim().toLowerCase();
        String mobileNumber = requestDTO.getMobileNumber().trim();

        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException(
                    "Email already registered: " + email
            );
        }

        if (userRepository.existsByMobileNumber(mobileNumber)) {
            throw new UserAlreadyExistsException(
                    "Mobile number already registered: " + mobileNumber
            );
        }

        User user = modelMapper.map(requestDTO, User.class);

        user.setEmail(email);
        user.setMobileNumber(mobileNumber);

        user.setPassword(
                passwordEncoder.encode(requestDTO.getPassword())
        );

        user.setRole(UserRole.CUSTOMER);

        user.setStatus(UserStatus.ACTIVE);

        User savedUser = userRepository.save(user);

        logger.info(
                "User registered successfully. userId={}, email={}",
                savedUser.getUserId(),
                savedUser.getEmail()
        );

        return modelMapper.map(
                savedUser,
                UserResponseDTO.class
        );
    }

    @Override
    public UserResponseDTO getUserById(Long userId) {

        logger.info("Fetching user details for userId: {}",
                userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with id: " + userId
                        ));

        logger.info("Successfully fetched user details for userId: {}",
                userId);

        return modelMapper.map(
                user,
                UserResponseDTO.class
        );
    }

    @Override
    public Page<UserResponseDTO> getAllUsers(
            int page,
            int size,
            String sortBy,
            String direction) {

        logger.info(
                "Fetching users with pagination and sorting. page={}, size={}, sortBy={}, direction={}",
                page,
                size,
                sortBy,
                direction
        );

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable =
                PageRequest.of(page, size, sort);

        Page<User> usersPage =
                userRepository.findAll(pageable);

        if (usersPage.isEmpty()) {
            throw new NoUsersFoundException(
                    "No users found in the system"
            );
        }

        Page<UserResponseDTO> responsePage =
                usersPage.map(user ->
                        modelMapper.map(
                                user,
                                UserResponseDTO.class
                        )
                );

        logger.info(
                "Successfully fetched {} users for page {}",
                responsePage.getNumberOfElements(),
                page
        );

        return responsePage;
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {

        logger.info("Starting soft delete for userId: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with id: " + userId
                        ));

        if (user.getStatus() == UserStatus.DELETED) {
            throw new IllegalStateException(
                    "User is already deleted"
            );
        }

        user.setStatus(UserStatus.DELETED);

        userRepository.save(user);

        logger.info(
                "User soft deleted successfully. userId={}, email={}",
                user.getUserId(),
                user.getEmail()
        );
    }

    @Override
    @Transactional
    public UserResponseDTO updateUser(
            Long userId,
            @Valid UpdateUserDTO updateDTO) {

        logger.info("Starting user update for userId: {}", userId);

        if (updateDTO.isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one field must be provided for update"
            );
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with id: " + userId
                        ));

        logger.info("Found user with id: {}, email: {}",
                userId,
                user.getEmail());
        if (user.getStatus() == UserStatus.DELETED) {
            throw new IllegalStateException(
                    "Cannot update a deleted user"
            );
        }

        applyUserNameUpdate(user, updateDTO);
        applyEmailUpdate(user, updateDTO);
        applyMobileUpdate(user, updateDTO);
        applyAgeUpdate(user, updateDTO);
        applyPasswordUpdate(user, updateDTO);

        User updatedUser = userRepository.save(user);

        logger.info("User updated successfully for userId: {}",
                userId);

        return modelMapper.map(
                updatedUser,
                UserResponseDTO.class
        );
    }

    private void applyUserNameUpdate(
            User user,
            UpdateUserDTO updateDTO) {

        if (updateDTO.getUserName() == null
                || updateDTO.getUserName().isBlank()) {
            return;
        }

        user.setUserName(updateDTO.getUserName().trim());

        logger.info("Updated user name");
    }

    private void applyEmailUpdate(
            User user,
            UpdateUserDTO updateDTO) {

        if (updateDTO.getEmail() == null
                || updateDTO.getEmail().isBlank()) {
            return;
        }

        String newEmail = updateDTO.getEmail().trim();

        if (!newEmail.equals(user.getEmail())
                && userRepository.existsByEmail(newEmail)) {

            throw new UserAlreadyExistsException(
                    "Email already registered: " + newEmail
            );
        }

        user.setEmail(newEmail);

        logger.info("Updated email");
    }

    private void applyMobileUpdate(
            User user,
            UpdateUserDTO updateDTO) {

        if (updateDTO.getMobileNumber() == null
                || updateDTO.getMobileNumber().isBlank()) {
            return;
        }

        String newMobile = updateDTO.getMobileNumber().trim();

        if (!newMobile.equals(user.getMobileNumber())
                && userRepository.existsByMobileNumber(newMobile)) {

            throw new UserAlreadyExistsException(
                    "Mobile number already registered: " + newMobile
            );
        }

        user.setMobileNumber(newMobile);

        logger.info("Updated mobile number");
    }

    private void applyAgeUpdate(
            User user,
            UpdateUserDTO updateDTO) {

        if (updateDTO.getAge() == null) {
            return;
        }

        user.setAge(updateDTO.getAge());

        logger.info("Updated age");
    }

    private void applyPasswordUpdate(
            User user,
            UpdateUserDTO updateDTO) {

        if (updateDTO.getPassword() == null
                || updateDTO.getPassword().isBlank()) {
            return;
        }

        user.setPassword(
                passwordEncoder.encode(updateDTO.getPassword())
        );

        logger.info("Updated password");
    }
}