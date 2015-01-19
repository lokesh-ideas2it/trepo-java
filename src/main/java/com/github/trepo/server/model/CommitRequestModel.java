package com.github.trepo.server.model;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * A Commit Request.
 * @author John Clark.
 */
public class CommitRequestModel {

    /**
     * The commit's author.
     */
    private String author;

    /**
     * The commit's email.
     */
    private String email;

    /**
     * The commit's message.
     */
    private String message;

    /**
     * Create a new Commit Request.
     * @param author The author.
     * @param email The email.
     * @param message The message.
     */
    public CommitRequestModel(String author, String email, String message) {
        this.author = author;
        this.email = email;
        this.message = message;
    }

    /**
     * Get the author.
     * @return The author.
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Get the email.
     * @return The email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Get the message.
     * @return The message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Validate the Commit Request.
     * If there are issues, throw a WebApplicationException
     */
    public void validate() {

        // TODO match each field against regex for further validation.

        if (author == null) {
            throw new WebApplicationException("author missing", Response.Status.BAD_REQUEST);
        }

        if (email == null) {
            throw new WebApplicationException("email missing", Response.Status.BAD_REQUEST);
        }

        if (message == null) {
            throw new WebApplicationException("message missing", Response.Status.BAD_REQUEST);
        }
    }
}
