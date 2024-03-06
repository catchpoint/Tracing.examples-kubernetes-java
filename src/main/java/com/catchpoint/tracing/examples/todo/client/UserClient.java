package com.catchpoint.tracing.examples.todo.client;

import com.catchpoint.tracing.examples.todo.http.HttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author sozal
 */
@Component
public class UserClient extends HttpClient {

    public UserClient(@Value("${user.service.protocol}") String protocol,
                      @Value("${user.service.host}") String host,
                      @Value("${user.service.port}") int port) {
        super(protocol, host, port);
    }

}
