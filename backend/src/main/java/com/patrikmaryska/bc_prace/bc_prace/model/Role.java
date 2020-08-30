package com.patrikmaryska.bc_prace.bc_prace.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Objects;

@Entity
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotBlank(message = "Authority is mandatory")
    @Size(min = 3, max = 40, message = "Size can be 3-40 characters.")
    @Column(length = 40, nullable = false)
    private String authority;

    @JsonIgnore
    @ManyToMany(mappedBy = "roles")
    private List<User> users;

    public Role(){

    }

    public Role(long id, String authority){
        this.id = id;
        this.authority = authority;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return authority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role)) return false;
        Role role = (Role) o;
        return getId() == role.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

}
