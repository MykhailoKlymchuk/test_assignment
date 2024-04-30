package com.test_assignment.model;

import com.test_assignment.util.PastDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDate;

// The User class represents the user model
@Data // Lombok annotation for generating getters, setters, toString, and other methods
@Builder // Lombok annotation for generating the Builder pattern
@NoArgsConstructor // Lombok annotation for generating a parameterless constructor
@AllArgsConstructor // Lombok annotation for generating a constructor with all parameters
public class User {

    @Email(message = "Please provide a valid email address") // Validation using @Email annotation to check the email format
    @NotBlank(message = "Email is required") // Validation using @NotBlank annotation to check for an empty string
    @Pattern(regexp = "^[a-zA-Z0-9.]{6,30}@[a-zA-Z0-9.]{2,15}\\.[a-zA-Z]{1,10}$", message = "Invalid email format") // Validation using @Pattern annotation with a regular expression
    private String email;

    @NotBlank(message = "First name is required") // Validation using @NotBlank annotation to check for an empty string
    private String firstName;

    @NotBlank(message = "Last name is required") // Validation using @NotBlank annotation to check for an empty string
    private String lastName;

    @PastDate // Custom validation using @PastDate annotation to check for a date in the past
    private LocalDate birthDate;

    private String address;

    @Pattern(regexp = "^\\+\\d{1,3}\\d{9}$", message = "Phone number must be 10 digits") // Validation using @Pattern annotation with a regular expression
    private String phoneNumber;
}
