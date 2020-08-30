package com.patrikmaryska.bc_prace.bc_prace.controller;

import com.patrikmaryska.bc_prace.bc_prace.model.RequestBody.PasswordObject;
import com.patrikmaryska.bc_prace.bc_prace.model.RequestBody.UserBearer;
import com.patrikmaryska.bc_prace.bc_prace.model.RequestBody.UserBody;
import com.patrikmaryska.bc_prace.bc_prace.model.Role;
import com.patrikmaryska.bc_prace.bc_prace.model.User;
import com.patrikmaryska.bc_prace.bc_prace.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.NoResultException;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserCotroller {

    @Autowired
    private UserService userService;

    private static Logger logger = LoggerFactory.getLogger("user");


    @PostMapping(value = "")
    @ResponseStatus(HttpStatus.OK)
    @Secured({"ROLE_ADMIN"})
    public void createUser(@Valid @RequestBody UserBody userBody, OAuth2Authentication auth) {

        List<Role> roles = new ArrayList<>();
        userBody.getRoles().forEach(s -> roles.add(userService.getRoleByName(s)));

        User user = new User(userBody.getFirstName(), userBody.getSurname(), userBody.getEmail(), userBody.getPassword());
        user.setActive(true);
        user.setRoles(roles);
        try {
            userService.createUser(user);
            logger.info("USER " + userBody.getEmail() + " has been created by " + auth.getName());
        } catch (Exception e) {
            logger.error("USER " + userBody.getEmail() + " could not have been created by" + auth.getName());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User email is already in use.");
        }
    }

    @GetMapping(value = "/roles")
    @Secured({"ROLE_ADMIN"})
    public List<Role> getUsersRoles() {
        return userService.getAllRoles();
    }


    @PutMapping("")
    @Secured("ROLE_ADMIN")
    public void updateUser(@Valid @RequestBody UserBody user, OAuth2Authentication auth) {

        List<Role> roles = new ArrayList<>();
        user.getRoles().forEach(s -> roles.add(userService.getRoleByName(s)));

        User u = userService.findUserById(user.getId());
        u.setFirstName(user.getFirstName());
        u.setSurname(user.getSurname());
        u.setRoles(roles);
        u.setEmail(user.getEmail());

        try {
            userService.updateUser(u);
            logger.info("USER " + user.getEmail() + " has been updated by " + auth.getName());
        } catch (Exception e) {
            logger.error("USER " + user.getEmail() + " could not have been updated by" + auth.getName());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User email is already in use.");
        }
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity deactivateUser(@PathVariable("id") long id, OAuth2Authentication auth) {
        try {
            userService.deactivateUser(id);
            logger.info("User with id " + id + " has been deactivated by " + auth.getName());
        } catch (NoResultException exception) {
            logger.warn("User with id" + id + " could not have been deactivated, because it does not exist. Processed by " + auth.getName());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No user was found.");
        } catch (Exception e) {
            logger.error("User with id" + id + " could not have been deactivated, because internal server error. Processed by " + auth.getName());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User could not have been deactivated. Something went wrong.");
        }

        return new ResponseEntity(HttpStatus.OK);
    }


    @GetMapping("/finds")
    @Secured({("ROLE_DOCUMENT_CREATOR"), "ROLE_ADMIN"})
    public List<UserBearer> findByLike(@RequestParam("email") String surname, OAuth2Authentication oauth, @RequestParam int page) {

        if (surname.matches("^[\\p{L}\\s.’\\-,]+$")) {
            return userService.getUsersBySurname(surname, page);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Surname is not in the correct format.");
        }
    }

    @GetMapping("/finds/active")
    @Secured({("ROLE_DOCUMENT_CREATOR"), "ROLE_ADMIN"})
    public List<User> findByLikeActive(@RequestParam("email") String surname, OAuth2Authentication oauth, @RequestParam int page) {

        if (surname.matches("^[\\p{L}\\s.’\\-,]+$")) {
            return userService.findByLikeActive(surname, oauth.getName(), page);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Surname is not in the correct format.");
        }
    }

    @PostMapping("/password")
    @Secured({("ROLE_USER"), ("ROLE_DOCUMENT_CREATOR"), "ROLE_ADMIN"})
    public void changePassword(@Valid @RequestBody PasswordObject passwordObject, OAuth2Authentication auth) {
        try {
            userService.changePassword(auth.getName(), passwordObject.getOldPassword(), passwordObject.getNewPassword());
        } catch (InternalError e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
