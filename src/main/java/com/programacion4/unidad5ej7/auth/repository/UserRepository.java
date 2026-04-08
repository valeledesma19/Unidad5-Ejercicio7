package com.programacion4.unidad5ej7.auth.repository;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

import com.programacion4.unidad5ej7.auth.models.UserEntity;

public interface UserRepository extends CrudRepository<UserEntity, Long> {

	Optional<UserEntity> findByUsername(String username);

	boolean existsByUsername(String username);
}
