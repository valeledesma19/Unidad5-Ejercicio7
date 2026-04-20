package com.programacion4.unidad5ej7.auth.services.interfaces;

import com.programacion4.unidad5ej7.auth.dtos.request.*;
import com.programacion4.unidad5ej7.auth.dtos.response.AuthResponseDto;

public interface IAuthService {

    void register(RegisterRequestDto request);

    AuthResponseDto login(LoginRequestDto request);

    AuthResponseDto refreshToken(RefreshTokenRequestDto request);
}