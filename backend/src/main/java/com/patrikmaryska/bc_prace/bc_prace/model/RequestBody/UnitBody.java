package com.patrikmaryska.bc_prace.bc_prace.model.RequestBody;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;


public class UnitBody {

    @NotBlank(message = "Name cannot be empty")
    @Size(min = 3, max = 40, message = "Size must be 3-40 chars")
    @Pattern(regexp = "^[a-zá-žA-ZÁ-Ž0-9\\)\\(\\!\\?\\s\\,\\(\\)\\\"]+$",  message = "Name of the group can contain only ?!,()")
    private String name;

    @NotNull(message = "IDS cannot be empty")
    private List<Long> ids;

    public UnitBody(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }
}
