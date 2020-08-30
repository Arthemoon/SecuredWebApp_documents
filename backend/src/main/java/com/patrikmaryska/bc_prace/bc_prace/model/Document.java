package com.patrikmaryska.bc_prace.bc_prace.model;

import com.fasterxml.jackson.annotation.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.*;

@Entity
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(unique = true, length = 60, nullable = false)
    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotBlank(message = "Title is mandatory")
    @Size(min = 3, max = 15, message = "Size can be 3-15 chars.")
    @Column(length = 15, nullable = false)
    @Pattern(regexp = "^[a-zá-žA-ZÁ-Ž0-9\\)\\(\\!\\?\\s]+$",  message = "Title can have alphanumeric characters and ? ! and whitespace chars")
    private String title;

    @NotBlank(message = "Description is mandatory")
    @Size(min = 3, max = 255,  message = "Size can be 3-255 chars.")
    @Column(length = 255, nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "document_state_id", nullable = false)
    private DocumentState documentState;

    @JsonIgnore
    @Column(name = "resource_path", length = 255, nullable = false)
    private String resourcePath;

    @Column(name = "upload_datetime", columnDefinition = "DATETIME", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull(message = "UploadDateTime is mandatory")
    private Date uploadDatetime;

    @Column(name = "approval_end_time", columnDefinition = "DATETIME", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull(message = "Approval end time is mandatory")
    private Date approvalEndTime;

    @Column(name = "active_start_time", columnDefinition = "DATETIME", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull(message = "Active start time is mandatory")
    private Date activeStartTime;

    @Column(name = "active_end_time", columnDefinition = "DATETIME", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull(message = "Active end time is mandatory")
    private Date activeEndTime;

    @OneToMany(mappedBy = "document", cascade= CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<UsersDocuments> documentsForUsers = new HashSet<>();

    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @Transient
    @JsonInclude
    private int approval;

    @Transient
    @JsonInclude
    private Date applicationStartTime;


    public Document() {
    }


    public Document(String title, String description){
        this.title = title;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Document)) return false;
        Document document = (Document) o;
        return getId() == document.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public Date getUploadDatetime() {
        return uploadDatetime;
    }

    public void setUploadDatetime(Date uploadDatetime) {
        this.uploadDatetime = uploadDatetime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<UsersDocuments> getDocumentsForUsers() {
        return documentsForUsers;
    }

    public void setDocumentsForUsers(Set<UsersDocuments> documentsForUsers) {
        this.documentsForUsers = documentsForUsers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getApprovalEndTime() {
        return approvalEndTime;
    }

    public void setApprovalEndTime(Date approvalEndTime) {
        this.approvalEndTime = approvalEndTime;
    }

    public Date getActiveStartTime() {
        return activeStartTime;
    }

    public void setActiveStartTime(Date activeStartTime) {
        this.activeStartTime = activeStartTime;
    }

    public Date getActiveEndTime() {
        return activeEndTime;
    }

    public void setActiveEndTime(Date activeEndTime) {
        this.activeEndTime = activeEndTime;
    }

    public DocumentState getDocumentState() {
        return documentState;
    }

    public void setDocumentState(DocumentState documentState) {
        this.documentState = documentState;
    }

    @Override
    public String toString() {
        return "Document{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", documentState=" + documentState +
                ", resourcePath='" + resourcePath + '\'' +
                ", uploadDatetime=" + uploadDatetime +
                ", approvalEndTime=" + approvalEndTime +
                ", activeStartTime=" + activeStartTime +
                ", activeEndTime=" + activeEndTime +
                ", documentsForUsers=" + documentsForUsers +
                ", user=" + user +
                '}';
    }

    public int getApproval() {
        return approval;
    }

    public void setApproval(int approval) {
        this.approval = approval;
    }

    public Date getApplicationStartTime() {
        return applicationStartTime;
    }

    public void setApplicationStartTime(Date applicationStartTime) {
        this.applicationStartTime = applicationStartTime;
    }
}

