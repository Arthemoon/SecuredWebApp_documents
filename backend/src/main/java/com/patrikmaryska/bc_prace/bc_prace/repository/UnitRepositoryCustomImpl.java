package com.patrikmaryska.bc_prace.bc_prace.repository;

import com.patrikmaryska.bc_prace.bc_prace.model.RequestBody.Group;
import com.patrikmaryska.bc_prace.bc_prace.model.Unit;
import com.patrikmaryska.bc_prace.bc_prace.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class UnitRepositoryCustomImpl implements UnitRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private UserRepository userRepository;

    private final int PAGE_SIZE = 10;

    @Override
    public List<Group> getUsersUnits(String email, int page) {
        TypedQuery<Unit> unitTypedQuery = entityManager.createQuery("SELECT u FROM Unit u WHERE u.user.email = :id ORDER BY u.name", Unit.class);
        unitTypedQuery.setParameter("id", email);
        if(page != 9999){
            unitTypedQuery.setFirstResult((page-1) * PAGE_SIZE);
            unitTypedQuery.setMaxResults(PAGE_SIZE);
        }

        List<Unit> units = unitTypedQuery.getResultList();

        return units.stream()
                .map(unit -> new Group(unit.getId(), unit.getName(), unit.getUsers()))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void updateUnit(Unit unit) {

        entityManager.merge(unit);
    }

    @Override
    public void removeUserFromGroups(long id) {
        Query query = entityManager.createNativeQuery("delete from users_units WHERE user_id=:id");
        Query docsQuery = entityManager.createNativeQuery("delete from users_documents WHERE user_id=:id");

        query.setParameter("id", id);
        docsQuery.setParameter("id", id);

        query.executeUpdate();
        docsQuery.executeUpdate();
    }
}
