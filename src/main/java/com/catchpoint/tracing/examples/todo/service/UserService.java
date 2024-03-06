package com.catchpoint.tracing.examples.todo.service;

import com.catchpoint.tracing.examples.todo.model.User;

/**
 * @author sozal
 */
public interface UserService {

    User getUser(String email);
    
}
