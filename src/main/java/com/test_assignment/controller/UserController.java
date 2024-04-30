package com.test_assignment.controller;

import com.test_assignment.exception.APIException;
import com.test_assignment.model.User;
import com.test_assignment.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/v1/users")
@Validated
@Tag(name = "Users API")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Get all users", description = "Returns all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully"),
            @ApiResponse(responseCode = "400", description = " no users found or a server error (details in the exception)")
    })
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @PostMapping
    @Operation(summary = "Create a user", description = "Returns a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created"),
            @ApiResponse(responseCode = "400", description = "the user by this name exists or a server error (details in the exception)")
    })
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        User createdUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PatchMapping("/{email}")
    @Operation(summary = "Partial updates a user", description = "Partial updates a user as per the email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated user"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<User> updateUserField(@PathVariable String email,
                                                @RequestParam(name = "field") String updateField,
                                                @RequestBody String updatedValue) {
        return ResponseEntity.ok(userService.updateUserField(email, updateField, updatedValue));
    }

    @PutMapping("/{email}")
    @Operation(summary = "Update a user", description = "Update a user as per the email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated user"),
            @ApiResponse(responseCode = "400", description = "false validation"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<User> updateUser(@PathVariable String email, @Valid @RequestBody User user) {
        User updatedUser = userService.updateUser(email, user);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{email}")
    @Operation(summary = "Delete a user", description = "Delete a user as per the email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted user"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Void> deleteUser(@PathVariable String email) {
        userService.deleteUser(email);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/dateRange")
    @Operation(summary = "Search for users by birth date range",
            description = "Searches for users whose birth date falls within the specified range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved users"),
            @ApiResponse(responseCode = "400", description = "false validation"),
            @ApiResponse(responseCode = "404", description = "No users found in the specified date range")
    })
    public ResponseEntity<List<User>> searchUsersByBirthDateRange(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        List<User> usersInDateRange = userService.searchUsersByBirthDateRange(fromDate, toDate);
        return ResponseEntity.ok(usersInDateRange);
    }
}
