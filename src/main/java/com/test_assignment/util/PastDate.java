package com.test_assignment.util;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

// Annotation for validating that a date is in the past
@Target({ElementType.FIELD}) // Specifies that the annotation can be used only on fields
@Retention(RetentionPolicy.RUNTIME) // Specifies that the annotation will be available at runtime
@Constraint(validatedBy = PastDateValidator.class) // Links the annotation to the validator class
@Documented
public @interface PastDate {
    String message() default "Birth date must be in the past"; // Default error message if validation fails
    Class<?>[] groups() default {}; // Groups for constraint validation
    Class<? extends Payload>[] payload() default {}; // Payload for additional metadata
}