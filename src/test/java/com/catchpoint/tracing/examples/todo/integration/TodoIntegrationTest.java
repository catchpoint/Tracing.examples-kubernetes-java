package com.catchpoint.tracing.examples.todo.integration;

import com.catchpoint.tracing.examples.todo.ContextInitializedTest;
import com.catchpoint.tracing.examples.todo.model.Todo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.net.URIBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * @author sozal
 */
@SqlGroup({
        @Sql("/sql/test-data/todo.sql"),
        @Sql(scripts = "/sql/clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
class TodoIntegrationTest extends ContextInitializedTest {

    @Autowired
    private ObjectMapper mapper;

    @Test
    void testFindTodos() throws URISyntaxException {
        URI uri = new URIBuilder("/todos/list").build();
        ResponseEntity<List<Todo>> response = get(uri.toString(), new ParameterizedTypeReference<List<Todo>>() {
        });
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Todo> actual = response.getBody();
        assertThat(actual).extracting(Todo::getId, Todo::getTitle, Todo::isCompleted)
                .containsExactly(
                        tuple(1L, "Test-1", true),
                        tuple(2L, "Test-2", false),
                        tuple(3L, "Test-3", true)
                ).hasSize(3);
    }
    @Test
    void testAddTodo() throws URISyntaxException, JsonProcessingException {
        URI uri = new URIBuilder("/todos/add").build();
        Todo expected = new Todo();
        expected.setTitle("Test 1");
        ResponseEntity<Todo> response = post(uri.toString(), mapper.writeValueAsString(expected), Todo.class);
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Todo actual = response.getBody();
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isNotNull();
        assertThat(actual).extracting(Todo::getTitle, Todo::isCompleted)
                .containsExactly(expected.getTitle(), expected.isCompleted());
    }

    @Test
    void testUpdateTodo() throws URISyntaxException, JsonProcessingException {
        URI uri = new URIBuilder("/todos/update/2").build();
        Todo expected = new Todo();
        expected.setTitle("Test Update 1");
        expected.setCompleted(true);
        ResponseEntity<Todo> response = put(uri.toString(), mapper.writeValueAsString(expected),
                Todo.class);
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Todo actual = response.getBody();
        assertThat(actual).extracting(Todo::getId, Todo::getTitle, Todo::isCompleted).containsExactly(2L,
                expected.getTitle(), expected.isCompleted());
    }

    @Test
    void testDeleteTodo() throws URISyntaxException {
        URI uri = new URIBuilder("/todos/delete/3").build();
        ResponseEntity<Void> response = delete(uri.toString(), null, Void.class);
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        URI uriGetAllTodos = new URIBuilder("/todos/list").build();
        ResponseEntity<List<Todo>> responseGetAllTodos = get(uriGetAllTodos.toString(),
                new ParameterizedTypeReference<List<Todo>>() {
                });
        assertThat(responseGetAllTodos).isNotNull();
        assertThat(responseGetAllTodos.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Todo> actual = responseGetAllTodos.getBody();
        assertThat(actual).extracting(Todo::getId, Todo::getTitle, Todo::isCompleted)
                .containsExactly(
                        tuple(1L, "Test-1", true),
                        tuple(2L, "Test-2", false)
                )
                .doesNotContain(tuple(3L, "Test-3", true));
    }

    @Test
    void testDuplicateTodo() throws URISyntaxException {
        URI uri = new URIBuilder("/todos/duplicate/3").build();
        ResponseEntity<Todo> response = post(uri.toString(), null, Todo.class);
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Todo actual = response.getBody();
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isNotNull().isNotEqualTo(3L);
        assertThat(actual).extracting(Todo::getTitle, Todo::isCompleted).containsExactly("Test-3", true);
    }

    @Test
    void testClearCompletedTodo() throws URISyntaxException {
        URI uri = new URIBuilder("/todos/clear-completed").build();
        ResponseEntity<Void> response = post(uri.toString(), null, Void.class);
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        URI uriGetAllTodos = new URIBuilder("/todos/list").build();
        ResponseEntity<List<Todo>> responseGetAllTodos = get(uriGetAllTodos.toString(),
                new ParameterizedTypeReference<List<Todo>>() {
                });
        assertThat(responseGetAllTodos).isNotNull();
        assertThat(responseGetAllTodos.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Todo> actual = responseGetAllTodos.getBody();
        assertThat(actual).extracting(Todo::getId, Todo::getTitle, Todo::isCompleted)
                .containsExactly(
                        tuple(2L, "Test-2", false)
                ).hasSize(1);
    }

}
