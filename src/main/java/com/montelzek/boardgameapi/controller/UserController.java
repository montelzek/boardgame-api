package com.montelzek.boardgameapi.controller;

import com.montelzek.boardgameapi.dto.UserDTOs;
import com.montelzek.boardgameapi.model.User;
import com.montelzek.boardgameapi.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDTOs.UserResponse> getUserById(@PathVariable Long id) {
        UserDTOs.UserResponse userResponse = userService.getUserById(id);
        return ResponseEntity.ok(userResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTOs.UserResponse> updateUser(@PathVariable Long id,
            @Valid @RequestBody UserDTOs.UserUpdateRequest userUpdateRequest) {

        User user = userService.updateUser(userUpdateRequest, id);
        return ResponseEntity.ok(userService.getUserById(user.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>("User of id " + id + " was successfully removed!", HttpStatus.OK);
    }
}
