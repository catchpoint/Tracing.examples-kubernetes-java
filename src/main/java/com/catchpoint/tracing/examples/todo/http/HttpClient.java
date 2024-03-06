package com.catchpoint.tracing.examples.todo.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

/**
 * @author sozal
 */
public class HttpClient {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    private final String protocol;
    private final String host;
    private final int port;

    public HttpClient(String protocol, String host, int port) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
    }

    private static <T> T sneakyThrow(Throwable t) {
        HttpClient.<RuntimeException>sneakyThrowInternal(t);
        return (T) t;
    }

    private static <T extends Throwable> void sneakyThrowInternal(Throwable t) throws T {
        throw (T) t;
    }

    private String createURLWithPort(String path) {
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return this.protocol + "://" + this.host + ":" + this.port + path;
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException ex) {
            sneakyThrow(ex);
            return null;
        }
    }

    private <T> T fromJson(String json, Class<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (IOException ex) {
            sneakyThrow(ex);
            return null;
        }
    }

    private <R> R doRequest(HttpMethod httpMethod, HttpHeaders headers,
                            String path, String body, Class<R> responseType) throws HttpException {
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity requestEntity = new HttpEntity(body, headers);
        try {
            ResponseEntity<R> responseEntity =
                    restTemplate.exchange(
                            createURLWithPort(path),
                            httpMethod,
                            requestEntity,
                            responseType);
            if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                throw new HttpException(responseEntity.getStatusCode().value(), responseEntity.toString());
            }
            return responseEntity.getBody();
        } catch (HttpClientErrorException e) {
            throw new HttpException(e.getStatusCode().value(), e.getResponseBodyAsString());
        }
    }

    private <R> R doRequest(HttpMethod httpMethod,
                            String path, String body, Class<R> responseType) throws HttpException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return doRequest(httpMethod, headers, path, body, responseType);
    }

    private <R> R doRequest(HttpMethod httpMethod, HttpHeaders headers,
                            String path, Object body, Class<R> responseType) throws HttpException {
        return doRequest(httpMethod, headers, path, toJson(body), responseType);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private <R> R doRequest(HttpMethod httpMethod, HttpHeaders headers,
                            String path, String body, ParameterizedTypeReference<R> responseType) throws HttpException {
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity requestEntity = new HttpEntity(body, headers);
        try {
            ResponseEntity<R> responseEntity =
                    restTemplate.exchange(
                            createURLWithPort(path),
                            httpMethod,
                            requestEntity,
                            responseType);
            if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                throw new HttpException(responseEntity.getStatusCode().value(), responseEntity.toString());
            }
            return responseEntity.getBody();
        } catch (HttpClientErrorException e) {
            throw new HttpException(e.getStatusCode().value(), e.getResponseBodyAsString());
        }
    }

    private <R> R doRequest(HttpMethod httpMethod,
                            String path, String body, ParameterizedTypeReference<R> responseType) throws HttpException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return doRequest(httpMethod, headers, path, body, responseType);
    }

    private <R> R doRequest(HttpMethod httpMethod, HttpHeaders headers,
                            String path, Object body, ParameterizedTypeReference<R> responseType) throws HttpException {
        return doRequest(httpMethod, headers, path, toJson(body), responseType);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public <R> R get(String path, Class<R> responseType) throws HttpException {
        return doRequest(HttpMethod.GET, path, null, responseType);
    }

    public <R> R get(String path, ParameterizedTypeReference<R> responseType) throws HttpException {
        return doRequest(HttpMethod.GET, path, null, responseType);
    }

    public void post(String path) throws HttpException {
        doRequest(HttpMethod.POST, path, null, Void.class);
    }

    public <R> R post(String path, Object body, Class<R> responseType) throws HttpException {
        return doRequest(HttpMethod.POST, path, toJson(body), responseType);
    }

    public <R> R post(String path, String body, Class<R> responseType) throws HttpException {
        return doRequest(HttpMethod.POST, path, body, responseType);
    }

    public <R> R post(String path, Object body, ParameterizedTypeReference<R> responseType) throws HttpException {
        return doRequest(HttpMethod.POST, path, toJson(body), responseType);
    }

    public <R> R post(String path, String body, ParameterizedTypeReference<R> responseType) throws HttpException {
        return doRequest(HttpMethod.POST, path, body, responseType);
    }

}
