package com.montelzek.boardgameapi.mapper;

import com.montelzek.boardgameapi.dto.ReviewResponse;
import com.montelzek.boardgameapi.model.Game;
import com.montelzek.boardgameapi.model.Review;
import com.montelzek.boardgameapi.model.Role;
import com.montelzek.boardgameapi.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ReviewMapperTest {

    @Test
    void shouldMapReviewToReviewResponse() {
        // Arrange
        ReviewMapper reviewMapper = new ReviewMapper();

        Game game = Game.builder()
                .id(1L)
                .title("Test title")
                .description("Description")
                .minPlayers(2)
                .maxPlayers(8)
                .playTime(90)
                .publisher("Publisher")
                .releaseYear(2015)
                .build();

        User user = User.builder()
                .id(1L)
                .fullName("Test User")
                .email("test@example.com")
                .password("password")
                .role(Role.USER)
                .build();

        Review review = Review.builder()
                .id(1L)
                .game(game)
                .user(user)
                .rating(8)
                .comment("Comment")
                .createdAt(LocalDateTime.now())
                .build();
        // Act
        ReviewResponse reviewResponse = reviewMapper.mapToReviewResponse(review);
        // Assert
        assertEquals(reviewResponse.id(), review.getId());
        assertEquals(reviewResponse.gameId(), review.getGame().getId());
        assertEquals(reviewResponse.gameTitle(), review.getGame().getTitle());
        assertEquals(reviewResponse.userId(), review.getUser().getId());
        assertEquals(reviewResponse.email(), review.getUser().getEmail());
        assertEquals(reviewResponse.rating(), review.getRating());
        assertEquals(reviewResponse.comment(), review.getComment());
        assertEquals(reviewResponse.createdAt(), review.getCreatedAt());
    }

}