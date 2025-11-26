package com.montelzek.boardgameapi.service;

import com.montelzek.boardgameapi.dto.GameResponse;
import com.montelzek.boardgameapi.exception.ResourceNotFoundException;
import com.montelzek.boardgameapi.mapper.GameMapper;
import com.montelzek.boardgameapi.model.Game;
import com.montelzek.boardgameapi.model.User;
import com.montelzek.boardgameapi.repository.GameRepository;
import com.montelzek.boardgameapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CollectionServiceImpl Tests")
public class CollectionServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GameMapper gameMapper;

    @InjectMocks
    private CollectionServiceImpl collectionService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    private User user;
    private Game game1;
    private Game game2;
    private GameResponse gameResponse1;
    private GameResponse gameResponse2;

    private static final Long USER_ID = 1L;
    private static final Long GAME_ID_1 = 10L;
    private static final Long GAME_ID_2 = 20L;
    private static final Long NON_EXISTENT_ID = -999L;

    @BeforeEach
    void setUp() {

        game1 = Game.builder()
                .id(GAME_ID_1)
                .title("Catan")
                .description("Trade and build settlements")
                .minPlayers(3)
                .maxPlayers(4)
                .playTime(90)
                .publisher("KOSMOS")
                .releaseYear(1995)
                .build();

        game2 = Game.builder()
                .id(GAME_ID_2)
                .title("Ticket to Ride")
                .description("Railway adventure")
                .minPlayers(2)
                .maxPlayers(5)
                .playTime(60)
                .publisher("Days of Wonder")
                .releaseYear(2004)
                .build();

        user = User.builder()
                .id(USER_ID)
                .email("test@example.com")
                .fullName("Test User")
                .games(new HashSet<>(Set.of(game1)))
                .build();

        gameResponse1 = new GameResponse(
                game1.getId(),
                game1.getTitle(),
                game1.getDescription(),
                game1.getMinPlayers(),
                game1.getMaxPlayers(),
                game1.getPlayTime(),
                game1.getPublisher(),
                game1.getReleaseYear(),
                Collections.emptySet(),
                Collections.emptyList()
        );

        gameResponse2 = new GameResponse(
                game2.getId(),
                game2.getTitle(),
                game2.getDescription(),
                game2.getMinPlayers(),
                game2.getMaxPlayers(),
                game2.getPlayTime(),
                game2.getPublisher(),
                game2.getReleaseYear(),
                Collections.emptySet(),
                Collections.emptyList()
        );
    }

    @Nested
    @DisplayName("getUserCollection() tests")
    class getUserCollectionTests {

        @Test
        @DisplayName("Should return list of games when user exists")
        void getUserCollection_whenUserExists_shouldReturnListOfGames() {
            // arrange
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
            when(gameMapper.mapToGameResponse(game1)).thenReturn(gameResponse1);

            // act
            List<GameResponse> result = collectionService.getUserCollection(USER_ID);

            // assert
            assertNotNull(result, "Result should not be null");
            assertEquals(1, result.size(), "Should return exactly one game");
            assertEquals(gameResponse1, result.getFirst(), "Should return correct game response");

            verify(userRepository).findById(USER_ID);
            verify(gameMapper).mapToGameResponse(game1);
        }

        @Test
        @DisplayName("Should return empty list when user has no games")
        void getUserCollection_whenUserHasNoGames_shouldReturnEmptyList() {
            // arrange
            User userWithNoGames = User.builder()
                    .id(USER_ID)
                    .email("empty@example.com")
                    .games(new HashSet<>())
                    .build();

            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(userWithNoGames));

            // act
            List<GameResponse> result = collectionService.getUserCollection(USER_ID);

            // assert
            assertNotNull(result);
            assertTrue(result.isEmpty(), "Should return empty list for user with no games");

            verify(userRepository).findById(USER_ID);
            verify(gameMapper, never()).mapToGameResponse(any());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when user does not exist")
        void getUserCollection_whenUserNotFound_ShouldThrowResourceNotFoundException() {
            // arrange
            when(userRepository.findById(NON_EXISTENT_ID)).thenReturn(Optional.empty());

            // act & assert
            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> collectionService.getUserCollection(NON_EXISTENT_ID),
                    "Should throw ResourceNotFoundException for non-existent user"
            );

            assertEquals(
                    "User not found with id: " + NON_EXISTENT_ID,
                    exception.getMessage(),
                    "Exception message should contain user ID"
            );

            verify(userRepository).findById(NON_EXISTENT_ID);
            verify(gameMapper, never()).mapToGameResponse(any());
        }

        @Test
        @DisplayName("Should return all games when user has multiple games")
        void getUserCollection_whenUserHasMultipleGames_shouldReturnAllGames() {
            // arrange
            User userWithManyGames = User.builder()
                    .id(USER_ID)
                    .email("example@example.com")
                    .games(new HashSet<>(Set.of(game1, game2)))
                    .build();

            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(userWithManyGames));
            when(gameMapper.mapToGameResponse(game1)).thenReturn(gameResponse1);
            when(gameMapper.mapToGameResponse(game2)).thenReturn(gameResponse2);

            // act
            List<GameResponse> result = collectionService.getUserCollection(USER_ID);

            // assert
            assertNotNull(result);
            assertEquals(2, result.size(), "Should return exactly two games");
            assertTrue(result.contains(gameResponse1), "Result should contain game1");
            assertTrue(result.contains(gameResponse2), "Result should contain game2");

            verify(userRepository).findById(USER_ID);
            verify(gameMapper, times(2)).mapToGameResponse(any(Game.class));
        }
    }

    @Nested
    @DisplayName("addGameCollection() tests")
    class AddGameToCollectionTests {

        @Test
        @DisplayName("Should add game to user's collection when authorized")
        void addGameToCollection_whenAuthorized_shouldAddGame() {
            // arrange
            when(userService.getCurrentUserId()).thenReturn(USER_ID);
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
            when(gameRepository.findById(GAME_ID_2)).thenReturn(Optional.of(game2));
            when(userRepository.save(any(User.class))).thenReturn(user);
            int gamesBefore = user.getGames().size();

            // act
            collectionService.addGameToCollection(USER_ID, GAME_ID_2);

            // assert
            verify(userRepository).save(userCaptor.capture());

            User savedUser = userCaptor.getValue();

            assertEquals(gamesBefore + 1, savedUser.getGames().size(),
                    "User should have one more game");
            assertTrue(savedUser.getGames().contains(game2),
                    "Collection should contain the added game");

            verify(userService).getCurrentUserId();
            verify(userRepository).findById(USER_ID);
            verify(gameRepository).findById(GAME_ID_2);
        }

        @Test
        @DisplayName("Should throw AccessDeniedException when not authorized")
        void addGameToCollection_whenNotAuthorized_shouldThrowAccessDeniedException() {
            // arrange
            Long anotherUserId = 999L;
            when(userService.getCurrentUserId()).thenReturn(anotherUserId);
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

            // act & assert
            AccessDeniedException exception = assertThrows(
                    AccessDeniedException.class,
                    () -> collectionService.addGameToCollection(USER_ID, GAME_ID_2)
            );

            assertEquals(
                    "You are not authorized to modify this collection",
                    exception.getMessage()
            );

            verify(userRepository, never()).save(any());
            verify(gameRepository, never()).findById(any());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when user does not exist")
        void addGameToCollection_whenUserNotFound_shouldThrowResourceNotFoundException() {
            // arrange
            when(userRepository.findById(NON_EXISTENT_ID)).thenReturn(Optional.empty());

            // act & assert
            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> collectionService.addGameToCollection(NON_EXISTENT_ID, GAME_ID_1)
            );

            assertEquals("User not found with id: " + NON_EXISTENT_ID, exception.getMessage());

            verify(userRepository).findById(NON_EXISTENT_ID);
            verify(gameRepository, never()).findById(any());
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when game does not exist")
        void addGameToCollection_whenGameNotFound_shouldThrowResourceNotFoundException() {
            // arrange
            when(userService.getCurrentUserId()).thenReturn(USER_ID);
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
            when(gameRepository.findById(NON_EXISTENT_ID)).thenReturn(Optional.empty());

            // act & assert
            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> collectionService.addGameToCollection(USER_ID, NON_EXISTENT_ID)
            );

            assertEquals("Game not found with id: " + NON_EXISTENT_ID, exception.getMessage());

            verify(userRepository).findById(USER_ID);
            verify(userService).getCurrentUserId();
            verify(gameRepository).findById(NON_EXISTENT_ID);
            verify(userRepository, never()).save(any());
        }
    }


}
