package com.github.trepo.server.model;

import javax.ws.rs.core.Response;

/**
 * @author John Clark.
 */
public class ExceptionModel {

    private Integer code;

    private String msg;

    public ExceptionModel(int status, String message) {
        code = status;
        msg = message;
    }

    public ExceptionModel(Response.Status status, String message) {
        code = status.getStatusCode();
        msg = message;
    }
}
