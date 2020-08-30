package com.patrikmaryska.bc_prace.bc_prace.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.patrikmaryska.bc_prace.bc_prace.service.DocumentService;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name = "document_state")
public class DocumentState {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotBlank(message = "State is mandatory")
    @Size(min=1, max=60, message = "Size can be 1-60 chars.")
    @Column(length = 60, nullable = false)
    private String state;

    @JsonIgnore
    @OneToMany(mappedBy = "documentState", cascade= CascadeType.ALL)
    private List<Document> documents;

    public DocumentState(){

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }
}
