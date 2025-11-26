package com.montelzek.boardgameapi.service;

import com.montelzek.boardgameapi.dto.GameResponse;
import com.montelzek.boardgameapi.dto.ReviewResponse;
import com.montelzek.boardgameapi.dto.UserResponse;
import com.montelzek.boardgameapi.dto.UserUpdateRequest;
import com.montelzek.boardgameapi.exception.ResourceNotFoundException;
import com.montelzek.boardgameapi.mapper.GameMapper;
import com.montelzek.boardgameapi.mapper.ReviewMapper;
import com.montelzek.boardgameapi.model.User;
import com.montelzek.boardgameapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

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
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        List<GameResponse> collection = user.getGames().stream()
                .map(gameMapper::mapToGameResponse)
                .collect(Collectors.toList());

        List<ReviewResponse> reviews = user.getReviews().stream()
                .map(reviewMapper::mapToReviewResponse)
                .collect(Collectors.toList());

        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().name(),
                collection,
                reviews
        );
    }

    @Override
    public User updateUser(UserUpdateRequest userUpdateRequest, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (!getCurrentUserId().equals(user.getId())) {
            throw new AccessDeniedException("You are not authorized to update this user");
        }

        if (userRepository.existsByEmail(userUpdateRequest.email()) &&
                !userUpdateRequest.email().equals(user.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already taken");
        }

        user.setFullName(userUpdateRequest.fullName());
        user.setEmail(userUpdateRequest.email());
        
        String newPassword = userUpdateRequest.password();
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
