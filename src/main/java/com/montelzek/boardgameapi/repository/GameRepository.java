package com.montelzek.boardgameapi.repository;

import com.montelzek.boardgameapi.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    @Query("select g from Game g join fetch g.categories where g.id=:id")
    Optional<Game> findGameByIdWithCategories(@Param("id") Long id);
}
