package com.catchpoint.tracing.examples.todo.model;

import javax.validation.constraints.NotNull;

/**
 * @author sozal
 */
public class Todo {

    private Long id;
    @NotNull(message = "Title cannot be null")
    private String title;
    private boolean completed;
    private long createdAt;

    public Todo() {
    }

    public Todo(Long id, String title, boolean completed, long createdAt) {
        this.id = id;
        this.title = title;
        this.completed = completed;
        this.createdAt = createdAt != 0 ? createdAt : System.currentTimeMillis();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

}
