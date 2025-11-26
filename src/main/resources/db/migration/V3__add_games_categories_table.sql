CREATE TABLE IF NOT EXISTS games_categories (
    game_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    PRIMARY KEY (game_id, category_id),
    FOREIGN KEY (game_id) REFERENCES games(id),
    FOREIGN KEY (category_id) REFERENCES categories(id)
);