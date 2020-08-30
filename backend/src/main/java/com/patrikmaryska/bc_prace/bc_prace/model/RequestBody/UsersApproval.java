package com.patrikmaryska.bc_prace.bc_prace.model.RequestBody;


import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;

public class UsersApproval {
    private String firstName;
    private String surname;
    private int approval;

    public UsersApproval(){

    }

    public UsersApproval(String firstName, String surname, int approval) {
        this.firstName = firstName;
        this.surname = surname;
        this.approval = approval;
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

    public int getApproval() {
        return approval;
    }

    public void setApproval(int approval) {
        this.approval = approval;
    }
}

