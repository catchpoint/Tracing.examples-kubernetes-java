package com.catchpoint.tracing.examples.todo.service;

import com.catchpoint.tracing.examples.todo.entity.TodoEntity;
import com.catchpoint.tracing.examples.todo.model.Todo;
import com.catchpoint.tracing.examples.todo.repository.TodoRepository;
import com.catchpoint.tracing.examples.todo.service.impl.TodoServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

/**
 * @author sozal
 */
@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @InjectMocks
    TodoServiceImpl service;

    @Mock
    TodoRepository repository;

    @Test
    void testFindTodos() {
        List<TodoEntity> expected = Arrays.asList(new TodoEntity(1L, "Test-1", true, 100000000L),
                new TodoEntity(2L, "Test-2", false, 100000000L), 
                new TodoEntity(3L, "Test-3", true, 100000000L));
        when(repository.findAll()).thenReturn(expected);
        List<Todo> actual = service.findTodos();
        assertThat(actual).usingRecursiveComparison()
                .usingDefaultComparator()
                .isEqualTo(expected);
    }

    @Test
    void testAddTodo() {
        Todo expected = new Todo();
        expected.setTitle("Test-1");
        when(repository.save(any(TodoEntity.class))).thenAnswer((Answer<TodoEntity>) invocationOnMock -> {
            TodoEntity entity = invocationOnMock.getArgument(0, TodoEntity.class);
            entity.setId(1L);
            return entity;
        });
        Todo actual = service.addTodo(expected);
        assertThat(actual).extracting(Todo::getId, Todo::getTitle, Todo::isCompleted)
                .containsExactly(1L, expected.getTitle(), expected.isCompleted());
    }

    @Test
    void testUpdateTodoWithNotExistingId() {
        Todo expected = new Todo();
        expected.setTitle("Test-1");
        when(repository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.updateTodo(1L, expected));
    }

    @Test
    void testUpdateTodo() {
        Todo expected = new Todo();
        expected.setTitle("Test-1");
        TodoEntity entity = new TodoEntity(1L, "Test", true, 100000000L);
        when(repository.findById(anyLong())).thenReturn(Optional.of(entity));
        when(repository.save(any(TodoEntity.class))).thenAnswer((Answer<TodoEntity>) invocationOnMock -> invocationOnMock.getArgument(0, TodoEntity.class));
        Todo actual = service.updateTodo(1L, expected);
        assertThat(actual).extracting(Todo::getId, Todo::getTitle, Todo::isCompleted)
                .containsExactly(1L, expected.getTitle(), expected.isCompleted());
    }

    @Test
    void testDuplicateTodoWithNotExistingId() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.duplicateTodo(1L));
    }

    @Test
    void testDuplicateTodo() {
        TodoEntity expected = new TodoEntity(1L, "Test", true, 100000000L);
        when(repository.findById(anyLong())).thenReturn(Optional.of(expected));
        when(repository.save(any(TodoEntity.class))).thenAnswer((Answer<TodoEntity>) invocationOnMock -> {
            TodoEntity entity = invocationOnMock.getArgument(0, TodoEntity.class);
            entity.setId(2L);
            return entity;
        });
        Todo actual = service.duplicateTodo(1L);
        assertThat(actual).extracting(Todo::getId, Todo::getTitle, Todo::isCompleted)
                .containsExactly(2L, expected.getTitle(), expected.isCompleted());
    }

}
