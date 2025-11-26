package com.montelzek.boardgameapi.service;

import com.montelzek.boardgameapi.dto.GameResponse;
import com.montelzek.boardgameapi.mapper.GameMapper;
import com.montelzek.boardgameapi.model.Game;
import com.montelzek.boardgameapi.model.User;
import com.montelzek.boardgameapi.repository.GameRepository;
import com.montelzek.boardgameapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
    private static final Long NON_EXISTENT_ID = 999L;

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
}
