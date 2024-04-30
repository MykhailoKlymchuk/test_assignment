package com.test_assignment.service;

import com.test_assignment.model.User;

import java.time.LocalDate;
import java.util.List;

public interface UserService {

    User createUser(User user);

    User updateUser(String email, User user);

    void deleteUser(String email);

    User updateUserField(String email, String updateField, String updatedValue);

    List<User> searchUsersByBirthDateRange(LocalDate fromDate, LocalDate toDate);

    List<User> findAll();
}


