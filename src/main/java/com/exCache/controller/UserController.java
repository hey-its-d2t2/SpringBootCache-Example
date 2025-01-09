package com.exCache.controller;

import com.exCache.entity.User;
import com.exCache.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/allUsers")
    public ResponseEntity<List<User>> getAllUsers(){
        List<User> users = userService.getAllUser();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/createUser")
    public ResponseEntity<User> createUser(@RequestBody User user){
        User createdUser = userService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }

    @PutMapping("/updateUser/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user){
        User updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/deleteUser/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
        //return ResponseEntity.ok(null);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/age/{age}")
    public ResponseEntity<Object> getUserByAge(@PathVariable Integer age){
        try {
            return ResponseEntity.ok(userService.getUserByAge(age));
        }catch (Exception e){
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
}
