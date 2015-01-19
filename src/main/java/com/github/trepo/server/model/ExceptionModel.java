package com.github.trepo.server.model;

import javax.ws.rs.core.Response;

/**
 * Our standard Exception Model.
 * @author John Clark.
 */
public class ExceptionModel {

    /**
     * The http status code.
     */
    private Integer code;

    /**
     * The error message.
     */
    private String msg;

    /**
     * Create a new ExceptionModel.
     * @param status The http status code.
     * @param message The error message.
     */
    public ExceptionModel(int status, String message) {
        code = status;
        msg = message;
    }

    /**
     * Create a new ExceptionModel.
     * @param status The http status code.
     * @param message The error message.
     */
    public ExceptionModel(Response.Status status, String message) {
        code = status.getStatusCode();
        msg = message;
    }

    /**
     * The http status code.
     * @return The status code.
     */
    public Integer getCode() {
        return code;
    }

    /**
     * The error message.
     * @return The message.
     */
    public String getMsg() {
        return msg;
    }
}
