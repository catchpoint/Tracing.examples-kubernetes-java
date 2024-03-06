package com.catchpoint.tracing.examples.todo.repository;

import com.catchpoint.tracing.examples.todo.entity.TodoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author sozal
 */
public interface TodoRepository extends JpaRepository<TodoEntity, Long> {

    List<TodoEntity> findByCompletedIsTrue();
    
    @Modifying
    @Query("DELETE FROM TodoEntity t WHERE t.createdAt <= ?1")
    void deleteOldTodos(long timestamp);

}
