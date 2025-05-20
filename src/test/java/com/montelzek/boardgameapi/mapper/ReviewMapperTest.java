package com.montelzek.boardgameapi.mapper;

import com.montelzek.boardgameapi.dto.ReviewDTOs;
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
        ReviewDTOs.ReviewResponse reviewResponse = reviewMapper.mapToReviewResponse(review);
        // Assert
        assertEquals(reviewResponse.getId(), review.getId());
        assertEquals(reviewResponse.getGameId(), review.getGame().getId());
        assertEquals(reviewResponse.getGameTitle(), review.getGame().getTitle());
        assertEquals(reviewResponse.getUserId(), review.getUser().getId());
        assertEquals(reviewResponse.getEmail(), review.getUser().getEmail());
        assertEquals(reviewResponse.getRating(), review.getRating());
        assertEquals(reviewResponse.getComment(), review.getComment());
        assertEquals(reviewResponse.getCreatedAt(), review.getCreatedAt());
    }

}