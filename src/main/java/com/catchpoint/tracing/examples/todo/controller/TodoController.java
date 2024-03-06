package com.catchpoint.tracing.examples.todo.controller;

import com.catchpoint.tracing.examples.todo.client.UserClient;
import com.catchpoint.tracing.examples.todo.error.UserNotFoundException;
import com.catchpoint.tracing.examples.todo.grpc.GRPCUser;
import com.catchpoint.tracing.examples.todo.grpc.GRPCUserRequest;
import com.catchpoint.tracing.examples.todo.grpc.UserServiceGrpc;
import com.catchpoint.tracing.examples.todo.http.HttpException;
import com.catchpoint.tracing.examples.todo.model.User;
import com.catchpoint.tracing.examples.todo.service.TodoService;
import com.catchpoint.tracing.examples.todo.model.Todo;
import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author sozal
 */
@RestController
@RequestMapping("/todos")
public class TodoController {
    
    private static final List<String> USER_EMAIL_LIST = Arrays.asList(
            "cwhite@todo.app",
            "awoods@todo.app",
            "mgordon@todo.app",
            "lfox@todo.app",
            "rwood@todo.app",
            "jbrooks@todo.app",
            "eadams@todo.app",
            "ehunter@todo.app",
            "imoss@todo.app",
            "swilliams@todo.app" // Not exist
    );
    private static final Random RANDOM = new Random();

    private final TodoService service;
    private final UserClient userClient;

    @Value("${grpc.user.host:localhost}")
    private String grpcServerHost;

    @Value("${grpc.user.port:-1}")
    private int grpcServerPort;

    @Value("${grpc.user.enabled:false}")
    private boolean grpcUserEnabled;

    public TodoController(@Autowired TodoService service,
                          @Autowired UserClient userClient,
                          @Value("${user.check.enabled:false}") boolean userCheckEnabled) {
        this.service = service;
        this.userClient = userCheckEnabled ? userClient : null;
    }

    public void checkUserAccessByGRPC(String email) {
        if (grpcServerPort == -1) {
            return;
        }

        ManagedChannel channel = NettyChannelBuilder
                .forAddress(grpcServerHost, grpcServerPort)
                .usePlaintext()
                .build();
        UserServiceGrpc.UserServiceBlockingStub stub = UserServiceGrpc.newBlockingStub(channel);
        
        try {
            GRPCUser user = stub.getUser(GRPCUserRequest.newBuilder()
                    .setEmail(email)
                    .build());
            if (user == null) {
                throw new UserNotFoundException();
            }
        } catch (Exception e) {
            throw new UserNotFoundException(e);
        } finally {
            channel.shutdown();
        }
    }
    
    public void checkUserAccessByHTTP(String email) {
        if (userClient != null) {
            try {
                User user = userClient.get("/users/get/" + email, User.class);
                if (user == null) {
                    throw new UserNotFoundException();
                }
            } catch (HttpException e) {
                e.printStackTrace();
                if (e.getResponseCode() == HttpStatus.NOT_FOUND.value()) {
                    throw new UserNotFoundException(e);
                } else {
                    throw new ResponseStatusException(HttpStatus.resolve(e.getResponseCode()));
                }
            }
        }
    }

    public void checkUserAccess() {
        String userEmail = USER_EMAIL_LIST.get(RANDOM.nextInt(USER_EMAIL_LIST.size()));
        if (grpcUserEnabled) {
            checkUserAccessByGRPC(userEmail);
        } else {
            checkUserAccessByHTTP(userEmail);
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<Todo>> findTodos() {
        checkUserAccess();
        List<Todo> todos = service.findTodos();
        return ResponseEntity.ok(todos);
    }

    @PostMapping("/add")
    public ResponseEntity<Todo> addTodo(@Valid @RequestBody Todo request) {
        checkUserAccess();
        Todo todo = service.addTodo(request);
        return ResponseEntity.ok(todo);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Todo> updateTodo(@PathVariable Long id, @Valid @RequestBody Todo request) {
        checkUserAccess();
        Todo todo = service.updateTodo(id, request);
        return ResponseEntity.ok(todo);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        checkUserAccess();
        service.deleteTodo(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/duplicate/{id}")
    public ResponseEntity<Todo> duplicateTodo(@PathVariable Long id) {
        checkUserAccess();
        Todo todo = service.duplicateTodo(id);
        return ResponseEntity.ok(todo);
    }

    @PostMapping("/clear-completed")
    public ResponseEntity<Void> clearCompletedTodo() {
        checkUserAccess();
        service.clearCompletedTodo();
        return ResponseEntity.ok().build();
    }

}
