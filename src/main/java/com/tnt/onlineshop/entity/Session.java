package com.tnt.onlineshop.entity;

import java.time.LocalDateTime;
import java.util.Objects;

public class Session {
    private long id;
    private String token;
    private LocalDateTime expireDate;
    private User user;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(LocalDateTime expireDate) {
        this.expireDate = expireDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Session session = (Session) o;
        return id == session.id &&
                Objects.equals(token, session.token) &&
                Objects.equals(expireDate, session.expireDate) &&
                Objects.equals(user, session.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, token, expireDate, user);
    }

    @Override
    public String toString() {
        return "Session{" +
                "id=" + id +
                ", token='" + token + '\'' +
                ", expireDate=" + expireDate +
                ", user=" + user +
                '}';
    }
}
