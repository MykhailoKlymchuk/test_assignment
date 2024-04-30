package com.test_assignment.util;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

// Validator class for the PastDate annotation
public class PastDateValidator implements ConstraintValidator<PastDate, LocalDate> {

    @Override
    public void initialize(PastDate constraintAnnotation) {
    }

    // Method to check if the date is in the past
    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        LocalDate currentDate = LocalDate.now(); // Get the current date
        return value != null && value.isBefore(currentDate); // Validate that the date is before the current date
    }
}