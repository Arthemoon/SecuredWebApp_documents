package com.patrikmaryska.bc_prace.bc_prace.repository;

import com.patrikmaryska.bc_prace.bc_prace.model.RequestBody.Group;
import com.patrikmaryska.bc_prace.bc_prace.model.Unit;

import java.util.List;

public interface UnitRepositoryCustom {

    List<Group> getUsersUnits(String email, int page);

    void updateUnit(Unit unit);

    void removeUserFromGroups(long id);
}
