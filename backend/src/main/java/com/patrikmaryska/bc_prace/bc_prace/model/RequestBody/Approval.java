package com.patrikmaryska.bc_prace.bc_prace.model.RequestBody;

import org.springframework.context.ApplicationListener;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Approval {
    @NotBlank(message = "Approval cannot be empty")
    @Size(min = 1, max = 40, message = "Size must be 1-40 chars.")
    private String approval;

    @NotNull(message = "Doc_id must be set")
    private long doc_id;

    public Approval(){

    }

    public String getApproval() {
        return approval;
    }

    public void setApproval(String approval) {
        this.approval = approval;
    }

    public long getDoc_id() {
        return doc_id;
    }

    public void setDoc_id(long doc_id) {
        this.doc_id = doc_id;
    }
}
