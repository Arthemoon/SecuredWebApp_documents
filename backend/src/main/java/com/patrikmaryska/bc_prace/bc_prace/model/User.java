package com.patrikmaryska.bc_prace.bc_prace.model;

import com.fasterxml.jackson.annotation.*;
import org.springframework.web.method.annotation.ModelAttributeMethodProcessor;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "first_name", length = 40, nullable = false)
    @Size(min=3, max=40,  message = "Size must be 3-40 chars.")
    @NotBlank(message = "Firstname cannot be blank.")
    @Pattern(regexp = "^[\\p{L}\\s.’\\-,]+$", message = "First name does not seem to look like name. Try again")
    private String firstName;

    @Column(length = 40, nullable = false)
    @Size(min=3, max=40, message = "Size must be 3-40.")
    @NotBlank(message = "Surname cannot be empty.")
   // @Pattern(regexp = "^[\\p{L}\\s.’\\-,]+$",message = "First name does not seem to look like name. Try again")
    private String surname;

    @Column(length = 40, unique = true, nullable = false)
    @Size(min=3, max=40, message = "Size must be 3-40 chars.")
    @Email(message = "Email should be valid")
    private String email;

    @JsonIgnore
    @Column(length = 70, nullable = false)
    @Size(min=4, max=70, message = "Size can be 4-70 chars")
    @NotBlank(message = "Password cannot be empty")
    private String password;

    @NotNull(message = "Active must be set.")
    @Column(nullable = false)
    private boolean active;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"))
    @JsonIgnore
    private List<Role> roles;

    @JsonIgnore
    @ManyToMany(mappedBy = "users")
    private List<Unit> units;

    @JsonIgnore
    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<UsersDocuments> usersDocuments;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Document> sharedDocuments;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Unit> groups;

    public List<Unit> getGroups() {
        return groups;
    }

    public void setGroups(List<Unit> groups) {
        this.groups = groups;
    }

    public User() {

    }

    public User(String firstName, String surname, String email, String password) {
        this.firstName = firstName;
        this.surname = surname;
        this.email = email;
        this.password = password;
    }

    public Set<UsersDocuments> getUsersDocuments() {
        return usersDocuments;
    }

    public void setUsersDocuments(Set<UsersDocuments> usersDocuments) {
        this.usersDocuments = usersDocuments;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
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

    public void setRoles(List<Role> roles) {
        this.roles = roles;
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

    public List<Document> getSharedDocuments() {
        return sharedDocuments;
    }

    public void setSharedDocuments(List<Document> sharedDocuments) {
        this.sharedDocuments = sharedDocuments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return getId() == user.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "User{" +
                "firstName='" + firstName + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", roles=" + roles +
                '}';
    }

    public List<Unit> getUnits() {
        return units;
    }

    public void setUnits(List<Unit> units) {
        this.units = units;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}