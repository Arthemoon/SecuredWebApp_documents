package com.patrikmaryska.bc_prace.bc_prace.model.RequestBody;

import com.patrikmaryska.bc_prace.bc_prace.validation.ValidPassword;

import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

public class UserBody {
    @NotBlank(message = "First name cannot be empty")
    @Size(min = 3, max = 40, message = "Size can be 3-40 chars")
    @Pattern(regexp = "^[\\p{L}\\s.’\\-,]+$", message = "First name does not seem to look like name. Try again")
    private String firstName;

    @NotBlank(message = "Surname cannot be empty")
    @Size(min = 3, max = 40, message = "Size must be 3-40 chars")
    @Pattern(regexp = "^[\\p{L}\\s.’\\-,]+$",  message = "First name does not seem to look like name. Try again")
    private String surname;

    @NotBlank(message = "Email cannot be empty")
    @Size(min = 3, max = 40, message = "Size cannot be empty 3-40 chars")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 3, max = 40, message = "Size can be 3-40 chars.")
  //   @ValidPassword(message = "Password is not in the requested type.")
    private String password;

    @NotNull(message = "ID cannot be empty")
    private long id;

    @NotNull(message = "Roles cannot be empty.")
    private List<String> roles;

    public UserBody(){
        roles = new ArrayList<>();
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
