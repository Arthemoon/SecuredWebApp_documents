package com.patrikmaryska.bc_prace.bc_prace.repository;

import com.patrikmaryska.bc_prace.bc_prace.model.Role;
import com.patrikmaryska.bc_prace.bc_prace.model.User;

import java.util.List;
import java.util.Optional;

public interface UserCustomRepository {
    Optional<User> getUserByEmail(String email);
    List<Role> getUsersRoles(long user_id);
    Role findRoleByName(String name);
    void createUser(User u);

    void updateUser(User user);

    List<User> getUsersBySurname(String email, int page);

    List<User> getUsersByGroupId(List<Long> longs);

    List<User> getUsersForApprovingDocument(long docId);
    List<User> getUsersForReadingDocument(long docId);

    List<Role> getAllRoles();

    void testing();

    void removeUserFromUsersDocuments(long id);
    List<User> getActiveUsersBySurname(String surname, int page);

    void changePassword(String name, String newPassword);
}
