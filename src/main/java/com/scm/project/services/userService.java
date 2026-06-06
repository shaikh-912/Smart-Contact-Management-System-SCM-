package com.scm.project.services;

import java.util.List;
import java.util.Optional;

import com.scm.project.Entity.User;

public interface userService {

    User saveUser(User user);
    Optional<User> getUserById(String Id);
    Optional<User> updatUser(User user);
    void deletUserById(String Id);
    boolean isUserExist(String userId);
    boolean isUserExistByEmail(String email);
    List<User> getAllUsers();
    User findByEmail(String email);
}
