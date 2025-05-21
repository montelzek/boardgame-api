package com.montelzek.boardgameapi.service;

import com.montelzek.boardgameapi.dto.GameDTOs;
import com.montelzek.boardgameapi.dto.ReviewDTOs;
import com.montelzek.boardgameapi.mapper.GameMapper;
import com.montelzek.boardgameapi.mapper.ReviewMapper;
import com.montelzek.boardgameapi.model.Game;
import com.montelzek.boardgameapi.model.Review;
import com.montelzek.boardgameapi.model.Role;
import com.montelzek.boardgameapi.model.User;
import com.montelzek.boardgameapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

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

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private Game game;
    private Review review;
    private GameDTOs.GameResponse gameResponse;
    private ReviewDTOs.ReviewResponse reviewResponse;

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

        gameResponse = GameDTOs.GameResponse.builder()
                .id(10L)
                .title("Test Game")
                .build();
        reviewResponse = ReviewDTOs.ReviewResponse.builder()
                .id(20L)
                .comment("Test Review")
                .build();
    }
}
