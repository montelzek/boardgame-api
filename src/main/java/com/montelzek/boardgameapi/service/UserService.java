package com.montelzek.boardgameapi.service;

import com.montelzek.boardgameapi.dto.UserDTOs;
import com.montelzek.boardgameapi.model.User;

public interface UserService {

    UserDTOs.UserResponse getUserById(Long userId);

    User updateUser(UserDTOs.UserUpdateRequest userUpdateRequest, Long userId);

    void deleteUser(Long userId);
}
