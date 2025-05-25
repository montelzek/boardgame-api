package com.montelzek.boardgameapi.repository;

import com.montelzek.boardgameapi.model.Game;
import com.montelzek.boardgameapi.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByGame(Game game);

    Optional<Review> findByGameIdAndUserId(Long gameId, Long userId);
}
