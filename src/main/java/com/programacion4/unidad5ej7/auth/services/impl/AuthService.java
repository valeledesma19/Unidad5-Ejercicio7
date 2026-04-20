package com.programacion4.unidad5ej7.auth.services.impl;

import com.programacion4.unidad5ej7.auth.dtos.request.*;
import com.programacion4.unidad5ej7.auth.dtos.response.AuthResponseDto;
import com.programacion4.unidad5ej7.auth.jwt.*;
import com.programacion4.unidad5ej7.auth.models.*;
import com.programacion4.unidad5ej7.auth.repository.UserRepository;
import com.programacion4.unidad5ej7.config.exceptions.*;

import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;

import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import com.programacion4.unidad5ej7.auth.services.interfaces.IAuthService;

@Service
@AllArgsConstructor
public class AuthService implements IAuthService {

	private static final String TOKEN_TYPE_BEARER = "Bearer";

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final JwtProperties jwtProperties;
	private final UserDetailsService userDetailsService;

	@Transactional
	@Override
	public void register(RegisterRequestDto request) {

		if (userRepository.existsByUsername(request.username())) {
			throw new UserAlreadyExistsException();
		}

		UserEntity user = UserEntity.builder()
				.username(request.username())
				.password(passwordEncoder.encode(request.password()))
				.role(UserRole.ROLE_USER)
				.build();

		userRepository.save(user);
	}

	@Override
	public AuthResponseDto login(LoginRequestDto request) {
		try {

			Authentication authentication = authenticationManager.authenticate(
					UsernamePasswordAuthenticationToken.unauthenticated(
							request.username(),
							request.password()
					)
			);

			UserDetails principal = (UserDetails) authentication.getPrincipal();

			List<String> roles = principal.getAuthorities()
					.stream()
					.map(GrantedAuthority::getAuthority)
					.toList();

			String accessToken = jwtService.generateAccessToken(principal.getUsername(), roles);
			String refreshToken = jwtService.generateRefreshToken(principal.getUsername());

			return new AuthResponseDto(
					accessToken,
					refreshToken,
					TOKEN_TYPE_BEARER,
					jwtProperties.accessExpirationMs()
			);

		} catch (BadCredentialsException e) {
			throw new InvalidCredentialsException();
		}
	}

	@Override
	public AuthResponseDto refreshToken(RefreshTokenRequestDto request) {

		Claims claims = jwtService.parseValidClaims(request.refreshToken())
				.orElseThrow(InvalidCredentialsException::new);

		String type = claims.get(JwtClaimNames.TOKEN_TYPE, String.class);

		if (!"REFRESH".equals(type)) {
			throw new InvalidCredentialsException();
		}

		String username = claims.getSubject();

		UserDetails user = userDetailsService.loadUserByUsername(username);

		List<String> roles = user.getAuthorities()
				.stream()
				.map(GrantedAuthority::getAuthority)
				.toList();

		String newAccessToken = jwtService.generateAccessToken(username, roles);

		return new AuthResponseDto(
				newAccessToken,
				request.refreshToken(),
				TOKEN_TYPE_BEARER,
				jwtProperties.accessExpirationMs()
		);
	}
}
