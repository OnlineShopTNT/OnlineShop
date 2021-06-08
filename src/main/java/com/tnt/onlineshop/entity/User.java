package com.tnt.onlineshop.entity;

import java.util.Objects;

public class User {

    private long id;
    private String email;
    private int iterations;
    private String salt;
    private String hash;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.toLowerCase();
    }

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return id == user.id
                && iterations == user.iterations
                && Objects.equals(email == null ? null : email.toLowerCase(), user.email == null ? null : user.email.toLowerCase())
                && Objects.equals(salt, user.salt)
                && Objects.equals(hash, user.hash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, iterations, salt, hash);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", iterations=" + iterations +
                '}';
    }

}
