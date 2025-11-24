package com.montelzek.boardgameapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "games")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(name = "min_players", nullable = false)
    private Integer minPlayers;

    @Column(name = "max_players", nullable = false)
    private Integer maxPlayers;

    @Column(name = "play_time", nullable = false)
    private Integer playTime;

    private String publisher;

    @Column(name = "release_year")
    private Integer releaseYear;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    private Set<Review> reviews = new HashSet<>();

    @ManyToMany(mappedBy = "games")
    private Set<User> users = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "game_category",
               joinColumns = @JoinColumn(name = "game_id"),
               inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories;
}
