package com.catchpoint.tracing.examples.todo.service;

import com.catchpoint.tracing.examples.todo.model.Todo;

import java.util.List;

/**
 * @author sozal
 */
public interface TodoService {

    List<Todo> findTodos();

    Todo addTodo(Todo todo);

    Todo updateTodo(Long id, Todo todo);

    void deleteTodo(Long id);

    Todo duplicateTodo(Long id);

    void clearCompletedTodo();

    void clearOldTodos();

}
