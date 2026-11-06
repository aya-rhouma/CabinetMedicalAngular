package com.cabinet.service.interfaces;

import com.cabinet.dto.AuthResponse;
import com.cabinet.dto.ChangePasswordRequest;
import com.cabinet.dto.LoginRequest;
import com.cabinet.dto.ProfileRequest;
import com.cabinet.dto.ProfileResponse;
import com.cabinet.dto.RegisterRequest;

public interface IAuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse register(RegisterRequest request);
    void changePassword(String email, ChangePasswordRequest request);
    ProfileResponse getProfile(String email);
    /** Renvoie une nouvelle session : le login (email) peut avoir changé. */
    AuthResponse updateProfile(String email, ProfileRequest request);
}
