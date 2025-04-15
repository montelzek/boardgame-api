package com.montelzek.boardgameapi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "Board Game Collection API",
				version = "1.0.0",
				description = "API for managing board games collection, users and reviews."
		),
		security = {
				@SecurityRequirement(name = "bearerAuth")
		}
)
@SecurityScheme(
		name = "bearerAuth",
		type = SecuritySchemeType.HTTP,
		scheme = "bearer",
		bearerFormat = "JWT"
)
public class BoardGameCollectionApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BoardGameCollectionApiApplication.class, args);
	}

}
