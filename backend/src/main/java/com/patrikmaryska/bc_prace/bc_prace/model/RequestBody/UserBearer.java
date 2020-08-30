package com.patrikmaryska.bc_prace.bc_prace.model.RequestBody;

import com.patrikmaryska.bc_prace.bc_prace.model.Role;

import java.util.List;

public class UserBearer {

    private long id;
    private String firstName;
    private String surname;
    private List<Role> roles;
    private String email;
    private boolean active;

    public UserBearer(long id, String firstName, String surname, List<Role> roles, String email, boolean active){
        this.id = id;
        this.firstName = firstName;
        this.surname = surname;
        this.roles = roles;
        this.email = email;
        this.active = active;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

