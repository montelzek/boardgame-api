package com.montelzek.boardgameapi.service;

import com.montelzek.boardgameapi.dto.GameResponse;
import com.montelzek.boardgameapi.dto.ReviewResponse;
import com.montelzek.boardgameapi.dto.UserResponse;
import com.montelzek.boardgameapi.dto.UserUpdateRequest;
import com.montelzek.boardgameapi.exception.ResourceNotFoundException;
import com.montelzek.boardgameapi.mapper.GameMapper;
import com.montelzek.boardgameapi.mapper.ReviewMapper;
import com.montelzek.boardgameapi.model.Game;
import com.montelzek.boardgameapi.model.Review;
import com.montelzek.boardgameapi.model.Role;
import com.montelzek.boardgameapi.model.User;
import com.montelzek.boardgameapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private GameMapper gameMapper;

    @Mock
    private ReviewMapper reviewMapper;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private User anotherUser;
    private Game game;
    private Review review;
    private GameResponse gameResponse;
    private ReviewResponse reviewResponse;
    private UserUpdateRequest userUpdateRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .fullName("Test User")
                .email("test@example.com")
                .password("password")
                .role(Role.USER)
                .games(new HashSet<>())
                .reviews(new HashSet<>())
                .build();

        anotherUser = User.builder()
                .id(2L)
                .fullName("Test User 2")
                .email("test2@example.com")
                .password("password2")
                .role(Role.USER)
                .games(new HashSet<>())
                .reviews(new HashSet<>())
                .build();

        game = Game.builder()
                .id(10L)
                .title("Test Game")
                .build();
        review = Review.builder()
                .id(20L)
                .comment("Test Review")
                .build();

        user.getGames().add(game);
        user.getReviews().add(review);

        gameResponse = new GameResponse(
                10L,
                "Test Game",
                null,
                null,
                null,
                null,
                null,
                null,
                Collections.emptySet(),
                Collections.emptyList()
        );
        reviewResponse = new ReviewResponse(
                20L,
                game.getId(),
                game.getTitle(),
                user.getId(),
                user.getEmail(),
                null,
                "Test Review",
                null
        );

        userUpdateRequest = new UserUpdateRequest(
                "Updated Name",
                "update@email.com",
                "update123"
        );
    }

    private void mockAuthenticatedUser(User user) {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
    }

    @Test
    void getUserByIdTest_whenUserExists_shouldReturnUserResponse() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(gameMapper.mapToGameResponse(any(Game.class))).thenReturn(gameResponse);
        when(reviewMapper.mapToReviewResponse(any(Review.class))).thenReturn(reviewResponse);

        // Act
        UserResponse actualResponse = userService.getUserById(1L);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(user.getId(), actualResponse.id());
        assertEquals(user.getFullName(), actualResponse.fullName());
        assertEquals(user.getEmail(), actualResponse.email());
        assertEquals(user.getRole().name(), actualResponse.role());
        assertFalse(actualResponse.collection().isEmpty());
        assertEquals(gameResponse.id(), actualResponse.collection().getFirst().id());
        assertEquals(gameResponse.title(), actualResponse.collection().getFirst().title());
        assertFalse(actualResponse.reviews().isEmpty());
        assertEquals(reviewResponse.id(), actualResponse.reviews().getFirst().id());
        assertEquals(reviewResponse.comment(), actualResponse.reviews().getFirst().comment());

        verify(userRepository).findById(1L);
        verify(gameMapper).mapToGameResponse(game);
        verify(reviewMapper).mapToReviewResponse(review);
    }

    @Test
    void getUserByIdTest_whenUserExistsWithEmptyCollections_shouldReturnUserResponseWithEmptyLists() {
        // Arrange
        user.setGames(Collections.emptySet());
        user.setReviews(Collections.emptySet());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        UserResponse actualResponse = userService.getUserById(1L);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(user.getId(), actualResponse.id());
        assertTrue(actualResponse.collection().isEmpty());
        assertTrue(actualResponse.reviews().isEmpty());

        verify(userRepository).findById(1L);
    }

    @Test
    void getUserByIdTest_whenUserNotFound_shouldThrowResourceNotFoundException() {
        // Arrange
        Long userId = 2L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(userId));

        assertEquals("User not found with id: " + userId, exception.getMessage());
        verify(userRepository).findById(userId);
    }

    @Test
    void updateUserTest_whenUserUpdateOwnProfile_shouldUpdateAndReturnUser() {
        // Arrange
        Long userIdToUpdate = user.getId();
        mockAuthenticatedUser(user);
        when(userRepository.findById(userIdToUpdate)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(userUpdateRequest.email())).thenReturn(false);
        when(passwordEncoder.encode(userUpdateRequest.password())).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User updatedUser = userService.updateUser(userUpdateRequest, userIdToUpdate);

        // Assert
        assertNotNull(updatedUser);
        assertEquals(userUpdateRequest.fullName(), updatedUser.getFullName());
        assertEquals(userUpdateRequest.email(), updatedUser.getEmail());
        assertEquals("encodedNewPassword", updatedUser.getPassword());
        verify(userRepository).findById(userIdToUpdate);
        verify(userRepository).existsByEmail(userUpdateRequest.email());
        verify(passwordEncoder).encode(userUpdateRequest.password());
        verify(userRepository).save(user);
    }

    @Test
    void updateUserTest_whenUserUpdatesOwnProfile_withoutPasswordChange_shouldUpdateAndReturnUser() {
        // Arrange
        Long userIdToUpdate = user.getId();
        userUpdateRequest = new UserUpdateRequest(
                userUpdateRequest.fullName(),
                userUpdateRequest.email(),
                null
        );
        mockAuthenticatedUser(user);
        when(userRepository.findById(userIdToUpdate)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(userUpdateRequest.email())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User updatedUser = userService.updateUser(userUpdateRequest, userIdToUpdate);

        // Assert
        assertNotNull(updatedUser);
        assertEquals(userUpdateRequest.fullName(), updatedUser.getFullName());
        assertEquals(userUpdateRequest.email(), updatedUser.getEmail());
        assertEquals("password", updatedUser.getPassword());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository).save(user);
    }

    @Test
    void updateUserTest_whenUserNotFound_shouldThrowResourceNotFoundException() {
        // Arrange
        Long nonExistentUserId = -1L;
        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                userService.updateUser(userUpdateRequest, nonExistentUserId));
        assertEquals("User not found with id: " + nonExistentUserId, exception.getMessage());
        verify(userRepository).findById(nonExistentUserId);
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUserTest_whenUserUpdateSomeoneProfile_shouldAccessDeniedException() {
        // Arrange
        Long updateUserId = anotherUser.getId();

        mockAuthenticatedUser(user);
        when(userRepository.findById(updateUserId)).thenReturn(Optional.of(anotherUser));


        // Act & Assert
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () ->
                userService.updateUser(userUpdateRequest, updateUserId));
        assertEquals("You are not authorized to update this user", exception.getMessage());
        verify(userRepository).findById(updateUserId);
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUserTest_whenEmailAlreadyTaken_shouldThrowResponseStatusException() {
        // Arrange
        Long userIdToUpdate = user.getId();
        mockAuthenticatedUser(user);
        when(userRepository.findById(userIdToUpdate)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(userUpdateRequest.email())).thenReturn(true);

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                userService.updateUser(userUpdateRequest, userIdToUpdate));
        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("Email already taken", exception.getReason());
        verify(userRepository).findById(userIdToUpdate);
        verify(userRepository).existsByEmail(userUpdateRequest.email());
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUser_whenUserDeletesOwnProfile_shouldDeleteUser() {
        // Arrange
        Long userIdToDelete = user.getId();

        mockAuthenticatedUser(user);
        when(userRepository.findById(userIdToDelete)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).deleteById(userIdToDelete);

        // Act
        userService.deleteUser(userIdToDelete);

        // Assert
        verify(userRepository).findById(userIdToDelete);
        verify(userRepository).deleteById(userIdToDelete);
    }

    @Test
    void deleteUser_whenAdminDeletesUserProfile_shouldDeleteUser() {
        // Arrange
        Long userIdToDelete = anotherUser.getId();

        mockAuthenticatedUser(user);
        Collection<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
        doReturn(authorities).when(authentication).getAuthorities();
        when(userRepository.findById(userIdToDelete)).thenReturn(Optional.of(anotherUser));
        doNothing().when(userRepository).deleteById(userIdToDelete);

        // Act
        userService.deleteUser(userIdToDelete);

        // Assert
        verify(userRepository).findById(userIdToDelete);
        verify(userRepository).deleteById(userIdToDelete);
    }

    @Test
    void deleteUserTest_whenUserNotFound_shouldThrowResourceNotFoundException() {
        // Arrange
        Long nonExistentUserId = -1L;
        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                userService.deleteUser(nonExistentUserId));
        assertEquals("User not found with id: " + nonExistentUserId, exception.getMessage());
        verify(userRepository).findById(nonExistentUserId);
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void deleteUserTest_whenUserDeleteSomeoneProfile_shouldThrowAccessDeniedException() {
        // Arrange
        Long userIdToDelete = anotherUser.getId();

        mockAuthenticatedUser(user);
        when(userRepository.findById(userIdToDelete)).thenReturn(Optional.of(anotherUser));


        // Act & Assert
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () ->
                userService.deleteUser(userIdToDelete));
        assertEquals("You are not authorized to delete this user", exception.getMessage());
        verify(userRepository).findById(userIdToDelete);
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void getCurrentUserIdTest_whenUserAuthenticatedAndExists_shouldReturnUserId() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // Act
        Long currentUserId = userService.getCurrentUserId();

        // Assert
        assertEquals(user.getId(), currentUserId);
        verify(authentication).getName();
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void getCurrentUserId_whenUserAuthenticatedButNotFound_shouldThrowUsernameNotFoundException() {
        String email = "unknown@example.com";
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () ->
                userService.getCurrentUserId());

        assertEquals("User not found with email: " + email, exception.getMessage());
        verify(authentication).getName();
        verify(userRepository).findByEmail(email);
    }
}
