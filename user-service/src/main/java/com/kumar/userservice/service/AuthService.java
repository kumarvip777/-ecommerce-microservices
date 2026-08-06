package com.kumar.userservice.service;

import com.kumar.userservice.dto.LoginRequest;
import com.kumar.userservice.dto.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest loginRequest);

}