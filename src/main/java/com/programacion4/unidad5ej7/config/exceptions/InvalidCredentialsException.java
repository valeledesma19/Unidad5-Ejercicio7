package com.programacion4.unidad5ej7.config.exceptions;

import java.util.List;
import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends CustomException {

	public InvalidCredentialsException() {
		super("Credenciales inválidas", HttpStatus.UNAUTHORIZED, List.of("Credenciales inválidas"));
	}
}
