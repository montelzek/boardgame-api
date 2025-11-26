package com.montelzek.boardgameapi.service;

import com.montelzek.boardgameapi.dto.UserResponse;
import com.montelzek.boardgameapi.dto.UserUpdateRequest;
import com.montelzek.boardgameapi.model.User;

public interface UserService {

    UserResponse getUserById(Long userId);

    User updateUser(UserUpdateRequest userUpdateRequest, Long userId);

    void deleteUser(Long userId);
}
