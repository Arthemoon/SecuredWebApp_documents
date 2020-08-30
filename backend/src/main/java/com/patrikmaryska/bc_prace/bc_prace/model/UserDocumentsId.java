package com.patrikmaryska.bc_prace.bc_prace.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UserDocumentsId implements Serializable {

    @Column(name="user_id", nullable = false)
    private long user;

    @Column(name="document_id", nullable = false)
    private long document;

    @Column(name="sharing_type_id", nullable = false)
    private long sharingTypeId;

    public UserDocumentsId(){}

    public UserDocumentsId(long user, long document, long sharingTypeId) {
        this.user = user;
        this.document = document;
        this.sharingTypeId = sharingTypeId;
    }

    public UserDocumentsId(long user, long document) {
        this.user = user;
        this.document = document;
    }

    public long getUser() {
        return user;
    }

    public void setUser(long user) {
        this.user = user;
    }

    public long getDocument() {
        return document;
    }

    public void setDocument(long document) {
        this.document = document;
    }

    public long getSharingTypeId() {
        return sharingTypeId;
    }

    public void setSharingTypeId(long sharingTypeId) {
        this.sharingTypeId = sharingTypeId;
    }
}
