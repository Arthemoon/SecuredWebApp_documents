package com.patrikmaryska.bc_prace.bc_prace.model;

import com.fasterxml.jackson.annotation.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name="users_documents")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class UsersDocuments {

    @EmbeddedId
    @JsonIgnore
    private UserDocumentsId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("user")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("document")
    @JoinColumn(name = "document_id", nullable = false)
    @JsonIgnore
    private Document document;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("sharingType")
    @JoinColumn(name = "sharing_type_id", nullable = false)
    private SharingType sharingType;

    @Column(name = "application_date", columnDefinition = "DATETIME", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull(message = "Active time is mandatory")
    private Date applicationStartTime;

    @Column(name="approval", nullable = false)
    @NotNull
    private int approval = 2;

    @Column(name = "email_sent", nullable = false)
    @JsonIgnore
    private boolean emailSent;

    public UsersDocuments(){
    }

    public UsersDocuments(User user, Document document) {
        this.user = user;
        this.document = document;
        this.id = new UserDocumentsId(user.getId(), document.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UsersDocuments)) return false;
        UsersDocuments that = (UsersDocuments) o;
        return getUser().equals(that.getUser()) &&
                getDocument().equals(that.getDocument());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUser(), getDocument());
    }

    public UserDocumentsId getId() {
        return id;
    }

    public void setId(UserDocumentsId id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public int getApproval() {
        return approval;
    }

    public void setApproval(int approval) {
        this.approval = approval;
    }

    public SharingType getSharingType() {
        return sharingType;
    }

    public void setSharingType(SharingType sharingType) {
        this.sharingType = sharingType;
    }

    public boolean isEmailSent() {
        return emailSent;
    }

    public void setEmailSent(boolean emailSent) {
        this.emailSent = emailSent;
    }

    @Override
    public String toString() {
        return "UsersDocuments{" +
                "id=" + id +
                ", user=" + user +
                ", sharingType=" + sharingType +
                ", approval=" + approval +
                '}';
    }

    public Date getApplicationStartTime() {
        return applicationStartTime;
    }

    public void setApplicationStartTime(Date applicationStartTime) {
        this.applicationStartTime = applicationStartTime;
    }
}
