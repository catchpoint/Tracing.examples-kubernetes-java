package com.catchpoint.tracing.examples.todo.http;

/**
 * @author sozal
 */
public class HttpException extends RuntimeException {

    private final int responseCode;
    private final String responseMessage;

    public HttpException(int responseCode, String responseMessage) {
        super(responseMessage);
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

}
