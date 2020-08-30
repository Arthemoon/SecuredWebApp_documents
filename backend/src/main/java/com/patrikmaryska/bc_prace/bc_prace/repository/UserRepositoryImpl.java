package com.patrikmaryska.bc_prace.bc_prace.repository;

import com.patrikmaryska.bc_prace.bc_prace.model.Role;
import com.patrikmaryska.bc_prace.bc_prace.model.User;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserCustomRepository {
    @PersistenceContext
    private EntityManager entityManager;

    private final int PAGE_SIZE = 10;

    @Override
    public Optional<User> getUserByEmail(String email) {
        TypedQuery<User> query = entityManager.createQuery("SELECT u FROM User u WHERE u.email=:email AND u.active=true", User.class);
        query.setParameter("email", email);
        User u;

        try {
            u = query.getSingleResult();
        } catch (NoResultException e){
            return Optional.empty();
        }

        return Optional.of(u);
    }

    @Override
    public List<Role> getUsersRoles(long user_id) {
        TypedQuery<Role> query = entityManager.createQuery("SELECT r FROM Role r JOIN r.users u WHERE u.id = :id", Role.class);
        query.setParameter("id", user_id);

        List<Role> roles = query.getResultList();

        return roles;
    }

    @Override
    public Role findRoleByName(String name) {
        TypedQuery<Role> query = entityManager.createQuery("SELECT r FROM Role r WHERE r.authority=:role", Role.class);
        query.setParameter("role", name);

        Role role = query.getSingleResult();

        return role;
    }

    @Override
    public void createUser(User u) {
        entityManager.persist(u);
    }


    @Override
    public List<User> getUsersByGroupId(List<Long> ids) {

        String query = "SELECT u FROM User u join u.units r WHERE r.id=?1";
        String additional = " OR r.id=?";

        if(ids.size() > 1){
            for(int i = 1; i < ids.size(); i++){
                String num = (i+1) + "";
                query = query.concat(additional).concat(num);
            }
        }
        TypedQuery<User> user = entityManager.createQuery(query, User.class);

        for(int i = 0; i < ids.size(); i++){
            user.setParameter(i+1, ids.get(i));
        }

        return user.getResultList();
    }

    @Override
    public List<User> getUsersForApprovingDocument(long docId) {
        TypedQuery<User> userTypedQuery = entityManager.createQuery("SELECT du.user FROM Document d join d.documentsForUsers du " +
                "join du.sharingType st WHERE du.document.id = :docId AND st.id=1", User.class);

        userTypedQuery.setParameter("docId", docId);

        return userTypedQuery.getResultList();
    }

    @Override
    public List<User> getUsersForReadingDocument(long docId){
        TypedQuery<User> userTypedQuery = entityManager.createQuery("SELECT du.user FROM Document d join d.documentsForUsers du " +
                "join du.sharingType st WHERE du.document.id = :docId AND st.id=2", User.class);

        userTypedQuery.setParameter("docId", docId);

        return userTypedQuery.getResultList();
    }

    @Override
    public List<Role> getAllRoles() {
      TypedQuery<Role> roles = entityManager.createQuery("SELECT r FROM Role r", Role.class);

      return roles.getResultList();
    }

    @Override
    public void testing() {
        Query q = entityManager.createQuery("UPDATE User u SET u.active=true WHERE u.id=4 OR u.id=139 OR u.id=142 OR u.id=150 OR u.id=169");

        q.executeUpdate();
    }

    @Override
    public void removeUserFromUsersDocuments(long id) {
        Query query = entityManager.createNativeQuery("DELETE FROM users_documents ud WHERE ud.user_id=:id");
        query.setParameter("id", id);
    }

    @Override
    public void updateUser(User user) {
        entityManager.merge(user);
    }

    @Override
    public List<User> getUsersBySurname(String surname, int page) {
        TypedQuery<User> userQuery = entityManager.createQuery("SELECT u FROM User u" +
                " WHERE u.surname LIKE :surname ORDER BY u.surname", User.class);
        String like = surname + "%";
        userQuery.setParameter("surname", like);
        userQuery.setFirstResult((page-1) * PAGE_SIZE);
        userQuery.setMaxResults(PAGE_SIZE);

        return userQuery.getResultList();
    }


    @Override
    public List<User> getActiveUsersBySurname(String surname, int page) {
        TypedQuery<User> userQuery = entityManager.createQuery("SELECT u FROM User u WHERE u.surname LIKE :surname AND u.active=1", User.class);
        String like = surname + "%";
        userQuery.setParameter("surname", like);

        userQuery.setFirstResult((page-1) * PAGE_SIZE);
        userQuery.setMaxResults(PAGE_SIZE);

        return userQuery.getResultList();
    }

    @Override
    public void changePassword(String name, String newPassword) {
        Query query = entityManager.createQuery("update User u SET u.password=:newPassword WHERE u.email=:email");
        query.setParameter("newPassword", newPassword);
        query.setParameter("email", name);

        query.executeUpdate();
    }

}
