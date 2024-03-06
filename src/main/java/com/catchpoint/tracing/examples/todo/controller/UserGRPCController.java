package com.catchpoint.tracing.examples.todo.controller;

import com.catchpoint.tracing.examples.todo.error.UserNotFoundException;
import com.catchpoint.tracing.examples.todo.grpc.GRPCUser;
import com.catchpoint.tracing.examples.todo.grpc.GRPCUserRequest;
import com.catchpoint.tracing.examples.todo.grpc.UserServiceGrpc.UserServiceImplBase;
import com.catchpoint.tracing.examples.todo.model.User;
import com.catchpoint.tracing.examples.todo.service.UserService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * @author myildiz
 */
@GrpcService
@Profile("user-service")
@Component
public class UserGRPCController extends UserServiceImplBase {
    
    @Autowired
    private UserService userService;

    @Override
    public void getUser(GRPCUserRequest request, StreamObserver<GRPCUser> responseObserver) {
        String email = request.getEmail();
        User user = userService.getUser(email);

        if (user == null) {
            responseObserver.onError(new UserNotFoundException());
            return;
        }

        GRPCUser response = GRPCUser.newBuilder()
                .setEmail(user.getEmail())
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
    
}
