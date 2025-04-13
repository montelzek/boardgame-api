package com.montelzek.boardgameapi.service;

import com.montelzek.boardgameapi.dto.GameDTOs;
import com.montelzek.boardgameapi.dto.ReviewDTOs;
import com.montelzek.boardgameapi.dto.UserDTOs;
import com.montelzek.boardgameapi.exception.ResourceNotFoundException;
import com.montelzek.boardgameapi.mapper.GameMapper;
import com.montelzek.boardgameapi.mapper.ReviewMapper;
import com.montelzek.boardgameapi.model.User;
import com.montelzek.boardgameapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final GameMapper gameMapper;
    private final ReviewMapper reviewMapper;

    @Override
    public UserDTOs.UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        List<GameDTOs.GameResponse> collection = user.getGames().stream()
                .map(gameMapper::mapToGameResponse)
                .collect(Collectors.toList());

        List<ReviewDTOs.ReviewResponse> reviews = user.getReviews().stream()
                .map(reviewMapper::mapToReviewResponse)
                .collect(Collectors.toList());

        return UserDTOs.UserResponse.builder()
                .id(user.getId())
                .fullName(user.getEmail())
                .email(user.getEmail())
                .role(user.getRole().name())
                .collection(collection)
                .reviews(reviews)
                .build();
    }

    @Override
    public User updateUser(UserDTOs.UserUpdateRequest userUpdateRequest, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (!getCurrentUserId().equals(user.getId())) {
            throw new AccessDeniedException("You are not authorized to update this user");
        }

        user.setFullName(userUpdateRequest.getFullName());
        user.setEmail(userUpdateRequest.getEmail());
        
        String newPassword = userUpdateRequest.getPassword();
        if (StringUtils.hasText(newPassword)) {
            user.setPassword(passwordEncoder.encode(newPassword));
        }

        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (!getCurrentUserId().equals(user.getId()) &&
                !SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new AccessDeniedException("You are not authorized to delete this user");
        }

        userRepository.deleteById(userId);
    }

    public Long getCurrentUserId() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .map(User::getId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

    }

}
