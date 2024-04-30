package com.test_assignment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.test_assignment.exception.APIException;
import com.test_assignment.exception.ResourceNotFoundException;
import com.test_assignment.model.User;
import com.test_assignment.service.UserService;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
class UserControllerTest {
/*
    @Autowired
    private HttpMessageConverter< Object > mappingJackson2HttpMessageConverter;
 */

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService service;

    @Autowired
    private ObjectMapper objectMapper;

    private User user1;
    private User user2;

    private final String url = "/api/v1/users";

    @BeforeEach
    void init() {


        user1 = User.builder()
                .email("john1.doe@example.com")
                .firstName("John")
                .lastName("Doe")
                .birthDate(LocalDate.parse("1994-04-30"))
                .address("New York")
                .phoneNumber("+1234567890")
                .build();
        user2 = User.builder()
                .email("john2.doe@example.com")
                .firstName("John")
                .lastName("Doe")
                .birthDate(LocalDate.parse("1999-04-30"))
                .address("New York")
                .phoneNumber("+1234567890")
                .build();
    }

    @Test
    void givenUsers_whenGetUsers_thenReturnsJsonArrayAllUsers() throws Exception {
        List<User> userList = Arrays.asList(user1);
        given(service.findAll()).willReturn(userList);

        mvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].email").value(user1.getEmail()))
                .andExpect(jsonPath("$[0].firstName").value(user1.getFirstName()))
                .andExpect(jsonPath("$[0].lastName").value(user1.getLastName()))
                .andExpect(jsonPath("$[0].birthDate").value(user1.getBirthDate().toString()))
                .andExpect(jsonPath("$[0].address").value(user1.getAddress()))
                .andExpect(jsonPath("$[0].phoneNumber").value(user1.getPhoneNumber()));
    }


    @Test
    void givenUsers_whenGetUsers_thenThrowException() throws Exception {
        given(service.findAll()).willThrow(new ResourceNotFoundException("User", "all", "No users found"));

        mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenValidUser_whenCreateUser_thenReturnsCreatedStatus() throws Exception {

        given(service.createUser(user1)).willReturn(user1);

        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user1)))
                .andExpect(status().isCreated());
    }

    @Test
    void givenInvalidUser_whenCreateUser_thenReturnsBadRequestStatus() throws Exception {
        given(service.createUser(user1)).willThrow(new APIException(HttpStatus.BAD_REQUEST, "Invalid user data"));

        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user1)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void givenValidFieldUpdate_whenUpdateUserField_thenReturnsOk() throws Exception {
        user1.setFirstName("Johnathan");
        when(service.updateUserField(anyString(), anyString(), anyString()))
                .thenReturn(user1);

        mvc.perform(MockMvcRequestBuilders.patch(url + "/" + user1.getEmail())
                        .param("field", "firstName")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"Johnathan\""))
                //.content(getJson("Johnathan")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Johnathan"));

    }


    @Test
    void givenInvalidFieldUpdate_whenUpdateUserField_thenReturnsNotFound() throws Exception {
        when(service.updateUserField(anyString(), anyString(), anyString()))
                .thenThrow(new ResourceNotFoundException("User", "email", user1.getEmail()));

        mvc.perform(MockMvcRequestBuilders.patch(url + "/" + user1.getEmail())
                        .param("field", "invalidField")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"UpdatedValue\"")) // Updated value
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void givenValidUserUpdate_whenUpdateUser_thenReturnsOk() throws Exception {
        when(service.updateUser(Mockito.anyString(), any(User.class)))
                .thenReturn(User.builder()
                        .email("john.doe@example.com")
                        .firstName("John")
                        .lastName("Doe")
                        .birthDate(LocalDate.now())
                        .address("New York")
                        .phoneNumber("+1234567890")
                        .build());

        mvc.perform(put("/api/v1/users/john.doe@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"email\": \"john.doe@example.com\", \"firstName\": \"John\", \"lastName\": \"Doe\", \"birthDate\": \"1990-01-01\", \"address\": \"New York\", \"phoneNumber\": \"+1234567890\" }"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void givenInvalidUserUpdate_whenUpdateUser_thenReturnsBadRequest() throws Exception {
        when(service.updateUser(Mockito.anyString(), any(User.class)))
                .thenThrow(new ValidationException("Validation failed"));

        mvc.perform(put(url + "/" + user1.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"email\": \"john.doe@example.com\", \"firstName\": \"\", \"lastName\": \"\", \"birthDate\": \"\", \"address\": \"\", \"phoneNumber\": \"\" }"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void givenNonExistingUser_whenUpdateUser_thenReturnsNotFound() throws Exception {
        when(service.updateUser(Mockito.anyString(), any(User.class))).thenThrow(ResourceNotFoundException.class);

        mvc.perform(put(url + "/nonexisting@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"email\": \"nonexisting@example.com\", \"firstName\": \"John\", \"lastName\": \"Doe\", \"birthDate\": \"1990-01-01\", \"address\": \"New York\", \"phoneNumber\": \"+1234567890\" }"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void givenExistingUser_whenDeleteUser_thenReturnsNoContent() throws Exception {
        doNothing().when(service).deleteUser(Mockito.anyString());

        mvc.perform(delete(url + "/" + user1.getEmail())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void givenNonExistingUser_whenDeleteUser_thenReturnsNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("User", "email", "john.doe@example.com")).when(service).deleteUser(anyString());

        mvc.perform(delete(url + "/" + user1.getEmail())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void givenValidDateRange_whenSearchUsersByBirthDateRange_thenReturnsUsers() throws Exception {
        List<User> usersInDateRange = List.of(user1, user2);
        doReturn(usersInDateRange).when(service).searchUsersByBirthDateRange(any(LocalDate.class), any(LocalDate.class));

        mvc.perform(get(url + "/dateRange")
                        .param("from", "1990-01-01")
                        .param("to", "2023-12-31")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(2));
    }

    @Test
    void givenInvalidDateRange_whenSearchUsersByBirthDateRange_thenReturnsBadRequest() throws Exception {
        when(service.searchUsersByBirthDateRange(any(LocalDate.class), any(LocalDate.class)))
                .thenThrow(new APIException(HttpStatus.BAD_REQUEST, "From date must be less than or equal to the to date"));

        mvc.perform(get(url + "/dateRange")
                        .param("from", "2023-12-31")
                        .param("to", "2020-01-01")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void givenEmptyDateRange_whenSearchUsersByBirthDateRange_thenReturnsNotFound() throws Exception {
        when(service.searchUsersByBirthDateRange(any(LocalDate.class), any(LocalDate.class)))
                .thenThrow(ResourceNotFoundException.class);

        mvc.perform(get(url + "/dateRange")
                        .param("from", "2023-01-01")
                        .param("to", "2023-12-31")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }


    /*
    protected String getJson( Object object ) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write( object,
                MediaType.APPLICATION_JSON, mockHttpOutputMessage );
        return mockHttpOutputMessage.getBodyAsString();
    }

     */

}