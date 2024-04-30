package com.test_assignment.service.impl;

import com.test_assignment.exception.APIException;
import com.test_assignment.exception.ResourceNotFoundException;
import com.test_assignment.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.HttpStatus;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private Map<String, User> users;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void init() {
        user = User.builder()
                .email("john.doe@example.com")
                .firstName("John")
                .lastName("Doe")
                .birthDate(LocalDate.now().minusYears(30))
                .address("New York")
                .phoneNumber("+1234567890")
                .build();
    }

    @Test
    void createUser_NewUser_ReturnsNewUser() {
        when(userService.createUser(user)).thenReturn(user);

        User createdUser = userService.createUser(user);

        assertEquals(user, createdUser);
    }

    @Test
    void createUser_ExistingUser_ThrowsAPIException() {
        when(users.containsKey(user.getEmail())).thenReturn(true);
        assertThrows(APIException.class, () -> userService.createUser(user));
    }

    @Test
    void updateUserField_ValidEmailAndField_ReturnsUpdatedUser() {
        String email = "john.doe@example.com";
        String updateField = "firstName";
        String updatedValue = "Johnathan";
        User existingUser = User.builder()
                .email(email)
                .firstName("John")
                .lastName("Doe")
                .birthDate(LocalDate.now().minusYears(30))
                .address("New York")
                .phoneNumber("+1234567890")
                .build();
        when(users.containsKey(email)).thenReturn(true);
        when(users.get(email)).thenReturn(existingUser);

        User updatedUser = userService.updateUserField(email, updateField, updatedValue);

        assertNotNull(updatedUser);
        assertEquals(updatedValue, updatedUser.getFirstName());
    }

    @Test
    void updateUserField_InvalidEmail_ThrowsResourceNotFoundException() {
        String email = "john.doe@example.com";
        String updateField = "firstName";
        String updatedValue = "Johnathan";
        when(users.containsKey(email)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> userService.updateUserField(email, updateField, updatedValue));
        verify(users, times(1)).containsKey(email);
        verify(users, never()).get(email);
        verify(users, never()).put(anyString(), any(User.class));
    }

    @Test
    void updateUser_ValidEmailAndUser_ReturnsUpdatedUser() {
        String email = "johnathan.doe@example.com";
        User updatedUser = User.builder()
                .email(email)
                .firstName("Johnathan")
                .lastName("Doe")
                .birthDate(LocalDate.now().minusYears(25))
                .address("New York")
                .phoneNumber("+1234567890")
                .build();
        User existingUser = User.builder()
                .email(email)
                .firstName("John")
                .lastName("Doe")
                .birthDate(LocalDate.now().minusYears(30))
                .address("New York")
                .phoneNumber("+1234567890")
                .build();
        when(users.containsKey(email)).thenReturn(true);
        when(users.get(email)).thenReturn(existingUser);

        User result = userService.updateUser(email, updatedUser);

        assertNotNull(result);
        assertEquals(updatedUser.getEmail(), result.getEmail());
        assertEquals(updatedUser.getFirstName(), result.getFirstName());
        assertEquals(updatedUser.getLastName(), result.getLastName());
        assertEquals(updatedUser.getBirthDate(), result.getBirthDate());
        assertEquals(updatedUser.getAddress(), result.getAddress());
        assertEquals(updatedUser.getPhoneNumber(), result.getPhoneNumber());
    }

    @Test
    void updateUser_NonexistentEmail_ThrowsResourceNotFoundException() {
        String email = "nonexistent@example.com";
        User updatedUser = User.builder()
                .email(email)
                .firstName("Johnathan")
                .lastName("Doe")
                .birthDate(LocalDate.now().minusYears(25))
                .address("New York")
                .phoneNumber("+1234567890")
                .build();
        when(users.containsKey(email)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(email, updatedUser));
        verify(users, never()).remove(anyString());
        verify(users, never()).put(anyString(), any(User.class));
    }

    @Test
    void deleteUser_ExistingEmail_RemovesUser() {
        String email = "john.doe@example.com";
        User existingUser = User.builder()
                .email(email)
                .firstName("John")
                .lastName("Doe")
                .build();
        when(users.containsKey(email)).thenReturn(true);
        when(users.remove(email)).thenReturn(existingUser);

        userService.deleteUser(email);

        verify(users, times(1)).remove(email);
    }

    @Test
    void deleteUser_NonexistentEmail_ThrowsResourceNotFoundException() {
        String email = "nonexistent@example.com";
        when(users.containsKey(email)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(email));
        verify(users, never()).remove(anyString());
    }

    @Test
    void searchUsersByBirthDateRange_ValidRange_ReturnsUsers() {
        LocalDate fromDate = LocalDate.of(1990, 1, 1);
        LocalDate toDate = LocalDate.of(2000, 12, 31);

        User user1 = User.builder()
                .email("john.doe@example.com")
                .firstName("John")
                .lastName("Doe")
                .birthDate(LocalDate.of(1995, 5, 10))
                .build();

        User user2 = User.builder()
                .email("jane.smith@example.com")
                .firstName("Jane")
                .lastName("Smith")
                .birthDate(LocalDate.of(1988, 8, 20))
                .build();

        when(users.values()).thenReturn(List.of(user1, user2));

        List<User> usersInRange = userService.searchUsersByBirthDateRange(fromDate, toDate);

        assertEquals(1, usersInRange.size());
        assertTrue(usersInRange.contains(user1));
        assertFalse(usersInRange.contains(user2));
    }


    @Test
    void searchUsersByBirthDateRange_InvalidRange_ThrowsAPIException() {
        LocalDate fromDate = LocalDate.of(2000, 1, 1);
        LocalDate toDate = LocalDate.of(1990, 12, 31);

        APIException exception = assertThrows(APIException.class,
                () -> userService.searchUsersByBirthDateRange(fromDate, toDate));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(" From date 2000-01-01 must be less than to date 1990-12-31", exception.getMessage());
    }


    @Test
    void findAllUsers_ThrowsResourceNotFoundException() {

        when(users.isEmpty()).thenReturn(true);

        assertThrows(ResourceNotFoundException.class, () -> userService.findAll());
    }

    @Test
    void findAllUsers_ReturnsAllUsers() {
        User user1 = User.builder()
                .email("john.doe@example.com")
                .firstName("John")
                .lastName("Doe")
                .build();

        User user2 = User.builder()
                .email("jane.smith@example.com")
                .firstName("Jane")
                .lastName("Smith")
                .build();

        when(users.isEmpty()).thenReturn(false);
        when(users.values()).thenReturn(List.of(user1, user2));

        List<User> allUsers = userService.findAll();

        assertEquals(2, allUsers.size());
        assertTrue(allUsers.contains(user1));
        assertTrue(allUsers.contains(user2));
    }
}