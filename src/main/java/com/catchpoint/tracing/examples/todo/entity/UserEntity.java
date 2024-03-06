package com.catchpoint.tracing.examples.todo.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author sozal
 */
@Entity
@Table(name = "user")
public class UserEntity {

    @Id
    private String email;
    private String firstName;
    private String lastName;
    private long createdAt;

    public UserEntity() {
        this.createdAt = System.currentTimeMillis();
    }

    public UserEntity(String email, String firstName, String lastName, long createdAt) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.createdAt = createdAt != 0 ? createdAt : System.currentTimeMillis();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

}
