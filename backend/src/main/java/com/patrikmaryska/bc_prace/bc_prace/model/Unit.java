package com.patrikmaryska.bc_prace.bc_prace.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;

@Entity
public class Unit {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotBlank(message = "Name of unit is mandatory")
    @Column(length = 40, nullable = false)
    @Size(min = 3, max = 40,  message = "Size can be 3-40 chars.")
    @Pattern(regexp = "^[a-zá-žA-ZÁ-Ž0-9\\)\\(\\!\\?\\s\\,\\(\\)\\\"]+$", message = "Name of the group can contain only ?!,()")
    private String name;

    public Unit(){}

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull
    private User user;

    @JsonIgnore
    @ManyToMany()
    @JoinTable(
            name = "users_units",
            joinColumns = @JoinColumn(
                    name = "unit_id", referencedColumnName = "id", nullable = false),
            inverseJoinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id", nullable = false))
    private List<User> users;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
