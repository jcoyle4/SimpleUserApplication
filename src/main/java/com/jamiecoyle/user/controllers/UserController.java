package com.jamiecoyle.user.controllers;

import com.jamiecoyle.user.models.User;
import com.jamiecoyle.user.models.UserUpdateDTO;
import com.jamiecoyle.user.repositories.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<User>> list() {
        return new ResponseEntity<>(userRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping
    @RequestMapping("{id}")
    public ResponseEntity<User> get(@PathVariable Long id) {
        User user = new User();
        Optional<User> userOptional = userRepository.findById(id);
        if (!userOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        BeanUtils.copyProperties(userRepository.getOne(id), user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<User> create(@RequestBody final UserUpdateDTO userUpdateDTO) {
        if (userUpdateDTO.getEmailAddress() == null || userUpdateDTO.getPassword() == null) {
            return ResponseEntity.badRequest().build();
        }
        User user = new User();
        user.setName(userUpdateDTO.getName());
        user.setEmailAddress(userUpdateDTO.getEmailAddress());
        user.setPassword(userUpdateDTO.getPassword());
        user.setLastLoginDate(getLocalDate());
        return ResponseEntity.ok(userRepository.saveAndFlush(user));
    }

    @DeleteMapping(value = "{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (!userOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody User user) {
        Optional<User> userOptional = userRepository.findById(id);
        if (!userOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        if (user.getName() == null || user.getEmailAddress() == null || user.getPassword() == null) {
            return ResponseEntity.noContent().build();
        }

        User existingUser = userRepository.getOne(id);
        // No need to change lastLoginDate
        BeanUtils.copyProperties(user, existingUser, "id", "lastLoginDate");
        return ResponseEntity.ok(user);
    }

    @PatchMapping(value = "{id}")
    public ResponseEntity<Object> partialUpdateUser(@PathVariable Long id, @RequestBody UserUpdateDTO userUpdateDTO) {
        Optional<User> userOptional = userRepository.findById(id);

        if (!userOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOptional.get();
        if (userUpdateDTO.getName() != null) {
            user.setName(userUpdateDTO.getName());
        }

        if (userUpdateDTO.getEmailAddress() != null) {
            user.setEmailAddress(userUpdateDTO.getEmailAddress());
        }

        if (userUpdateDTO.getPassword() != null) {
            user.setPassword(userUpdateDTO.getPassword());
        }

        return ResponseEntity.ok(userRepository.saveAndFlush(user));
    }

    @GetMapping
    // Assumption that the client will know the id of the user, alternative implementation also provided
    @RequestMapping("/{id}/login")
    public ResponseEntity<Object> login(@PathVariable Long id, @RequestBody UserUpdateDTO userUpdateDTO) {
        Optional<User> userOptional = userRepository.findById(id);

        if (!userOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOptional.get();
        if (user.getEmailAddress().equals(userUpdateDTO.getEmailAddress()) &&
                user.getPassword().equals(userUpdateDTO.getPassword())) {
            user.setLastLoginDate(getLocalDate());
            userRepository.saveAndFlush(user);
            return ResponseEntity.ok(user);
        }

        return ResponseEntity.badRequest().build();
    }

    @GetMapping
    @RequestMapping("/login")
    public ResponseEntity<Object> login(@RequestBody UserUpdateDTO userUpdateDTO) {
        String sql = "SELECT id FROM user WHERE email_address='" + userUpdateDTO.getEmailAddress() +"'";
        User user = new User();
        Connection connection = connect();
        try (
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                user = userRepository.getOne(resultSet.getLong("id"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            closeConnection(connection);
        }

        if (user.getEmailAddress().equals(userUpdateDTO.getEmailAddress()) &&
                user.getPassword().equals(userUpdateDTO.getPassword())) {

            user.setLastLoginDate(getLocalDate());
            userRepository.saveAndFlush(user);
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.badRequest().build();
    }

    private static LocalDate getLocalDate() {
        return LocalDate.now();
    }

    private Connection connect() {
        String url = "jdbc:sqlite:user.db";
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(url);
        }
        catch (SQLException e) {
            System.out.print(e);
        }

        return connection;
    }

    private void closeConnection(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
