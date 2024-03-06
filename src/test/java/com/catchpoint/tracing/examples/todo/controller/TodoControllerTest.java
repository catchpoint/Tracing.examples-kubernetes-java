package com.catchpoint.tracing.examples.todo.controller;

import com.catchpoint.tracing.examples.todo.client.UserClient;
import com.catchpoint.tracing.examples.todo.service.TodoService;
import com.catchpoint.tracing.examples.todo.model.Todo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author sozal
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = TodoController.class)
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
                                                  
    @MockBean
    private TodoService service;

    @MockBean
    private UserClient userClient;

    @Test
    void testFindTodos() throws Exception {
        List<Todo> expected = Arrays.asList(new Todo(1L, "Test-1", true,100000000L),
                new Todo(2L, "Test-2", false, 100000000L), 
                new Todo(3L, "Test-3", true, 100000000L));
        when(service.findTodos()).thenReturn(expected);
        MvcResult response = mockMvc.perform(get("/todos/list")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        List<Todo> actual = objectMapper.readValue(response.getResponse().getContentAsString(), new TypeReference<List<Todo>>() {
        });
        assertThat(actual).usingFieldByFieldElementComparator().containsExactlyElementsOf(expected).hasSize(3);
    }

    @Test
    void testAddTodoWithInvalidRequest() throws Exception {
        mockMvc.perform(post("/todos/add")
                .content(objectMapper.writeValueAsString(new Todo()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAddTodo() throws Exception {
        Todo expected = new Todo();
        expected.setTitle("Test-1");
        when(service.addTodo(any(Todo.class))).thenAnswer((Answer<Todo>) invocationOnMock -> {
            Todo todo = invocationOnMock.getArgument(0, Todo.class);
            todo.setId(1L);
            return todo;
        });
        MvcResult response = mockMvc.perform(post("/todos/add")
                .content(objectMapper.writeValueAsString(expected))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        Todo actual = objectMapper.readValue(response.getResponse().getContentAsString(), Todo.class);
        assertThat(actual).extracting(Todo::getId, Todo::getTitle, Todo::isCompleted)
                .containsExactly(1L, expected.getTitle(), expected.isCompleted());
    }

    @Test
    void testUpdateTodoWithInvalidRequest() throws Exception {
        mockMvc.perform(put("/todos/update/{id}", 1L)
                .content(objectMapper.writeValueAsString(new Todo()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateTodo() throws Exception {
        Todo expected = new Todo();
        expected.setTitle("Test-1");
        when(service.updateTodo(anyLong(), any(Todo.class))).thenAnswer((Answer<Todo>) invocationOnMock -> {
            Long id = invocationOnMock.getArgument(0, Long.class);
            Todo todo = invocationOnMock.getArgument(1, Todo.class);
            todo.setId(id);
            return todo;
        });
        MvcResult response = mockMvc.perform(put("/todos/update/{id}", 1L)
                .content(objectMapper.writeValueAsString(expected))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        Todo actual = objectMapper.readValue(response.getResponse().getContentAsString(), Todo.class);
        assertThat(actual).extracting(Todo::getId, Todo::getTitle, Todo::isCompleted)
                .containsExactly(1L, expected.getTitle(), expected.isCompleted());
    }

    @Test
    void testDeleteTodo() throws Exception {
        Todo expected = new Todo();
        expected.setTitle("Test-1");
        doNothing().when(service).deleteTodo(anyLong());
        mockMvc.perform(delete("/todos/delete/{id}", 1L)
                .content(objectMapper.writeValueAsString(expected))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent()).andReturn();
    }

    @Test
    void testDuplicateTodo() throws Exception {
        Todo expected = new Todo();
        expected.setId(1L);
        expected.setTitle("Test-1");
        expected.setCompleted(false);
        when(service.duplicateTodo(anyLong())).thenReturn(expected);
        MvcResult response = mockMvc.perform(post("/todos/duplicate/{id}", 1L)
                .content(objectMapper.writeValueAsString(expected))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        Todo actual = objectMapper.readValue(response.getResponse().getContentAsString(), Todo.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void testClearCompletedTodo() throws Exception {
        doNothing().when(service).clearCompletedTodo();
        mockMvc.perform(post("/todos/clear-completed")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
    }

}
