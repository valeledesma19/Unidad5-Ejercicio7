package com.programacion4.unidad5ej7.auth.controllers;

import com.programacion4.unidad5ej7.auth.dtos.request.*;
import com.programacion4.unidad5ej7.auth.dtos.response.AuthResponseDto;
import com.programacion4.unidad5ej7.auth.services.interfaces.IAuthService;
import com.programacion4.unidad5ej7.config.BaseResponse;

import jakarta.validation.Valid;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

	private final IAuthService authService;

	@PostMapping("/register")
	public ResponseEntity<BaseResponse<Void>> register(@Valid @RequestBody RegisterRequestDto request) {
		authService.register(request);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(BaseResponse.ok(null, "Usuario registrado correctamente"));
	}

	@PostMapping("/login")
	public ResponseEntity<BaseResponse<AuthResponseDto>> login(@Valid @RequestBody LoginRequestDto request) {
		AuthResponseDto body = authService.login(request);
		return ResponseEntity.ok(BaseResponse.ok(body, "Autenticación correcta"));
	}


	@PostMapping("/refresh")
	public ResponseEntity<BaseResponse<AuthResponseDto>> refresh(
			@Valid @RequestBody RefreshTokenRequestDto request) {

		AuthResponseDto body = authService.refreshToken(request);

		return ResponseEntity.ok(
				BaseResponse.ok(body, "Token renovado correctamente")
		);
	}
}
