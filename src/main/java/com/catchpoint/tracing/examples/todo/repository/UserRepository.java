package com.catchpoint.tracing.examples.todo.repository;

import com.catchpoint.tracing.examples.todo.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author sozal
 */
public interface UserRepository extends JpaRepository<UserEntity, String> {
}
