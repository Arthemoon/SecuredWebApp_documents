package com.patrikmaryska.bc_prace.bc_prace.service;

import com.patrikmaryska.bc_prace.bc_prace.model.RequestBody.Group;
import com.patrikmaryska.bc_prace.bc_prace.model.RequestBody.GroupBody;
import com.patrikmaryska.bc_prace.bc_prace.model.RequestBody.UnitBody;
import com.patrikmaryska.bc_prace.bc_prace.model.Unit;
import com.patrikmaryska.bc_prace.bc_prace.model.User;
import com.patrikmaryska.bc_prace.bc_prace.repository.UnitRepository;
import com.sun.jdi.InternalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class UnitService {
    @Autowired
    private UnitRepository unitRepository;
    @Autowired
    private UserService userService;

    private final int GROUP_MAX_NUMBER = 30;


    public void saveUnit(UnitBody unit, String email){

        User user =  userService.getUserByEmail(email).get();

        if(user.getGroups().size()+1 > GROUP_MAX_NUMBER){
            throw new IllegalStateException("You can have only 30 groups. Please remove some of your groups.");
        }
        List<User> users = userService.getUsersFromIds(unit.getIds());

        Unit un = new Unit();
        un.setName(unit.getName());
        un.setUser(user);
        un.setUsers(users);
        unitRepository.save(un);
    }

    public List<Group> getUsersUnits(String email, int page) {
         return unitRepository.getUsersUnits(email, page);
    }

    public void deleteUnit(long id, String email) throws AuthorizationServiceException {
        Unit unit = unitRepository.getOne(id);
        User user = userService.getUserByEmail(email).get();

        if(user.getGroups().contains(unit)){
            unitRepository.deleteById(id);
        } else {
            throw new AuthorizationServiceException("You do not have permission to delete this group.");
        }
    }

    public void updateUnit(GroupBody group, String email) throws AuthorizationServiceException {
        User user = userService.getUserByEmail(email).get();
        if(group.getIds().size() == 0){
            deleteUnit(group.getId(), email);
            return;
        }

        if(user.getGroups().stream().anyMatch(unit -> unit.getId() == group.getId())){
            Unit unit = new Unit();
            unit.setId(group.getId());
            unit.setName(group.getName());
            unit.setUser(user);
            unit.setUsers(userService.getUsersFromIds(group.getIds()));
            unitRepository.updateUnit(unit);
        } else {
            throw new AuthorizationServiceException("You cannot update this group.");
        }
    }

    public void removeUserFromGroups(long id) {
        unitRepository.removeUserFromGroups(id);
    }
}
