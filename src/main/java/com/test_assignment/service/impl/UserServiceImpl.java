package com.test_assignment.service.impl;

import com.test_assignment.exception.APIException;
import com.test_assignment.exception.ResourceNotFoundException;
import com.test_assignment.model.User;
import com.test_assignment.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    Map<String, User> users = new HashMap<>();

    @Value("${user.ageLimit}")
    private int ageLimit;
    @Value("${email.regex}")
    private String emailRegex;

    //@Value("${phoneNumber.regex}")
    private final String phoneNumberRegex="^\\+\\d{1,3}\\d{9}$";

    @Override
    public User createUser(User user) {
        if (users.containsKey(user.getEmail())) {
            throw new APIException(HttpStatus.BAD_REQUEST, "User with this email already exists");
        }

        checkAge(user.getBirthDate());

        users.put(user.getEmail(), user);
        return users.get(user.getEmail());
    }

    @Override
    public User updateUserField(String email, String updateField, String updatedValue) {
        if (!users.containsKey(email))
            throw new ResourceNotFoundException("User", "email", email);

        User existingUser = users.get(email);
        switch (updateField) {
            case "firstName" -> existingUser.setFirstName(updatedValue);
            case "lastName" -> existingUser.setLastName(updatedValue);
            case "birthDate" -> {
                try {
                    LocalDate newBirthDate = LocalDate.parse(updatedValue);
                    existingUser.setBirthDate(newBirthDate);
                } catch (DateTimeParseException e) {
                    throw new APIException(HttpStatus.BAD_REQUEST, "incorrect date format, it should be yyyy-mm-dd");
                }
            }
            case "email" -> {
                if (!updatedValue.matches(emailRegex))
                    throw new APIException(HttpStatus.BAD_REQUEST, "Invalid email format");

                users.remove(email);
                existingUser.setEmail(email);
                users.put(email, existingUser);
            }
            case "address" -> existingUser.setAddress(updatedValue);
            case "phoneNumber" -> {
                if (!updatedValue.matches(phoneNumberRegex))
                    throw new APIException(HttpStatus.BAD_REQUEST, "Invalid phoneNumber format");

                existingUser.setPhoneNumber(updatedValue);
            }
            default -> throw new APIException(HttpStatus.BAD_REQUEST, "incorrect field name");
        }

        return users.get(existingUser.getEmail());
    }

    @Override
    public User updateUser(String email, User updatedUser) {
        if (!users.containsKey(email))
            throw new ResourceNotFoundException("User", "email", email);

        checkAge(updatedUser.getBirthDate());

        if (emailRegex != null && !updatedUser.getEmail().matches(emailRegex)) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Invalid email format");
        }

        if (!updatedUser.getPhoneNumber().matches(phoneNumberRegex))
            throw new APIException(HttpStatus.BAD_REQUEST, "Invalid phoneNumber format");

        User existingUser = users.get(email);

        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setBirthDate(updatedUser.getBirthDate());
        existingUser.setAddress(updatedUser.getAddress());
        existingUser.setPhoneNumber(updatedUser.getPhoneNumber());

        if (!email.equals(updatedUser.getEmail())) {
            users.remove(email);
            users.put(existingUser.getEmail(), existingUser);
        }

        return existingUser;
    }


    @Override
    public void deleteUser(String email) {
        if (!users.containsKey(email))
            throw new ResourceNotFoundException("User", "email", email);
        users.remove(email);
    }

    @Override
    public List<User> searchUsersByBirthDateRange(LocalDate fromDate, LocalDate toDate) {
        if (fromDate.isAfter(toDate)) {
            throw new APIException(HttpStatus.BAD_REQUEST, "From date must be less than or equal to the to date");
        }

        List<User> usersInDateRange = new ArrayList<>();

        for (User user : users.values()) {
            LocalDate userBirthDate = user.getBirthDate();
            if (userBirthDate.isEqual(fromDate) || userBirthDate.isEqual(toDate) ||
                    (userBirthDate.isAfter(fromDate) && userBirthDate.isBefore(toDate))) {
                usersInDateRange.add(user);
            }
        }

        if (usersInDateRange.isEmpty()) {
            throw new APIException(HttpStatus.NOT_FOUND, "No users found in the specified date range");
        }

        return usersInDateRange;
    }


    private void checkAge(LocalDate birthDate) {
        LocalDate currentDate = LocalDate.now();
        int age = Period.between(birthDate, currentDate).getYears();
        if (age < ageLimit) {
            throw new APIException(HttpStatus.BAD_REQUEST, "User must be at least " + ageLimit + " years old");
        }
    }

    @Override
    public List<User> findAll() {
        if (users.isEmpty())
            throw new ResourceNotFoundException("User", "all", "No users found");

        return new ArrayList<>(users.values());
    }


}

