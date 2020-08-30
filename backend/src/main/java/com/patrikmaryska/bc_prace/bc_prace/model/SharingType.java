package com.patrikmaryska.bc_prace.bc_prace.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name = "sharing_type")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SharingType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;


    @NotBlank(message = "Name of sharing type is mandatory")
    @Column(length = 60, nullable = false)
    @Size(min = 3, max = 60,  message = "Size can be 3-60 chars.")
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "sharingType", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<UsersDocuments> usersDocuments;

    public SharingType(){

    }

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

    public List<UsersDocuments> getUsersDocuments() {
        return usersDocuments;
    }

    public void setUsersDocuments(List<UsersDocuments> usersDocuments) {
        this.usersDocuments = usersDocuments;
    }

    @Override
    public String toString() {
        return "SharingType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
