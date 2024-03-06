package com.catchpoint.tracing.examples.todo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.MySQLContainer;

/**
 * @author sozal
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = {ContextInitializedTest.Initializer.class})
public abstract class ContextInitializedTest {

    @LocalServerPort
    protected int localPort;

    @Autowired
    private TestRestTemplate testRestTemplate;

    private static final MySQLContainer MY_SQL_CONTAINER;

    static {
        MY_SQL_CONTAINER = new MySQLContainer("mysql:8.0");
        MY_SQL_CONTAINER.start();
    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            configurableApplicationContext.addApplicationListener((ApplicationListener<WebServerInitializedEvent>) event ->
                    org.testcontainers.Testcontainers.exposeHostPorts(event.getWebServer().getPort())
            );
            TestPropertyValues.of(
                    "spring.datasource.url=" + MY_SQL_CONTAINER.getJdbcUrl(),
                    "spring.datasource.username=" + MY_SQL_CONTAINER.getUsername(),
                    "spring.datasource.password=" + MY_SQL_CONTAINER.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    private String createURLWithPort(String path) {
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return "http://localhost:" + localPort + path;
    }

    protected <R> ResponseEntity<R> doRequest(HttpMethod httpMethod, String path,
                                              String body, Class<R> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return doRequest(httpMethod, headers, path, body, responseType);
    }

    protected <R> ResponseEntity<R> doRequest(HttpMethod httpMethod, String path,
                                              String body, ParameterizedTypeReference<R> parameterizedTypeReference) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return doRequest(httpMethod, headers, path, body, parameterizedTypeReference);
    }

    protected <R> ResponseEntity<R> doRequest(HttpMethod httpMethod, HttpHeaders headers,
                                              String path, String body, Class<R> responseType) {
        HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);
        return testRestTemplate.exchange(createURLWithPort(path), httpMethod, requestEntity, responseType);
    }

    protected <R> ResponseEntity<R> doRequest(HttpMethod httpMethod, HttpHeaders headers,
                                              String path, String body, ParameterizedTypeReference<R> responseType) {
        HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);
        return testRestTemplate.exchange(createURLWithPort(path), httpMethod, requestEntity, responseType);
    }

    protected <R> ResponseEntity<R> get(String path, Class<R> responseType) {
        return doRequest(HttpMethod.GET, path, null, responseType);
    }

    protected <R> ResponseEntity<R> get(String path, ParameterizedTypeReference<R> parameterizedTypeReference) {
        return doRequest(HttpMethod.GET, path, null, parameterizedTypeReference);
    }

    protected <R> ResponseEntity<R> post(String path, String body, Class<R> responseType) {
        return doRequest(HttpMethod.POST, path, body, responseType);
    }

    protected <R> ResponseEntity<R> post(String path, String body, ParameterizedTypeReference<R> parameterizedTypeReference) {
        return doRequest(HttpMethod.POST, path, body, parameterizedTypeReference);
    }

    protected <R> ResponseEntity<R> put(String path, String body, Class<R> responseType) {
        return doRequest(HttpMethod.PUT, path, body, responseType);
    }

    protected <R> ResponseEntity<R> put(String path, String body, ParameterizedTypeReference<R> parameterizedTypeReference) {
        return doRequest(HttpMethod.PUT, path, body, parameterizedTypeReference);
    }

    protected <R> ResponseEntity<R> patch(String path, String body, Class<R> responseType) {
        return doRequest(HttpMethod.PATCH, path, body, responseType);
    }

    protected <R> ResponseEntity<R> delete(String path, String body, Class<R> responseType) {
        return doRequest(HttpMethod.DELETE, path, body, responseType);
    }

    protected <R> ResponseEntity<R> delete(String path, String body, ParameterizedTypeReference<R> parameterizedTypeReference) {
        return doRequest(HttpMethod.DELETE, path, body, parameterizedTypeReference);
    }

}
