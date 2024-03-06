package com.catchpoint.tracing.examples.todo.service.impl;

import com.catchpoint.tracing.examples.todo.entity.TodoEntity;
import com.catchpoint.tracing.examples.todo.model.Todo;
import com.catchpoint.tracing.examples.todo.repository.TodoRepository;
import com.catchpoint.tracing.examples.todo.service.TodoService;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author sozal
 */
@Service
@Transactional
public class TodoServiceImpl implements TodoService {

    private static final long ONE_HOUR_AS_MILLIS =  60 * 60 * 1000;

    private final TodoRepository repository;

    public TodoServiceImpl(TodoRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Todo> findTodos() {
        List<TodoEntity> entities = repository.findAll();
        return entities.stream()
                .map(entity -> new Todo(entity.getId(), entity.getTitle(), entity.isCompleted(), entity.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Override
    public Todo addTodo(Todo request) {
        TodoEntity entity = new TodoEntity();
        entity.setTitle(request.getTitle());
        entity.setCompleted(request.isCompleted());
        entity = repository.save(entity);
        return new Todo(entity.getId(), entity.getTitle(), entity.isCompleted(), entity.getCreatedAt());
    }

    @Override
    public Todo updateTodo(Long id, Todo request) {
        TodoEntity entity = getTodoEntity(id);
        entity.setTitle(request.getTitle());
        entity.setCompleted(request.isCompleted());
        entity = repository.save(entity);
        return new Todo(entity.getId(), entity.getTitle(), entity.isCompleted(), entity.getCreatedAt());
    }

    @Override
    public void deleteTodo(Long id) {
        repository.deleteById(id);
    }

    @Override
    public Todo duplicateTodo(Long id) {
        TodoEntity duplicatedEntity = getTodoEntity(id);
        TodoEntity entity = new TodoEntity();
        entity.setTitle(duplicatedEntity.getTitle());
        entity.setCompleted(duplicatedEntity.isCompleted());
        entity = repository.save(entity);
        return new Todo(entity.getId(), entity.getTitle(), entity.isCompleted(), entity.getCreatedAt());
    }

    @Override
    public void clearCompletedTodo() {
        List<TodoEntity> completedTodos = repository.findByCompletedIsTrue();
        repository.deleteAllInBatch(completedTodos);
    }

    @Override
    public void clearOldTodos() {
        repository.deleteOldTodos(System.currentTimeMillis() - ONE_HOUR_AS_MILLIS);
    }

    private TodoEntity getTodoEntity(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Todo not found for given id."));
    }

}
