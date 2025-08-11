package org.example.multidbapp.model.response.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response object containing basic user information")
public record UserResponse(
        @Schema(description = "Unique identifier of the user", example = "1")
        Long id,
        @Schema(description = "User's email address used as username", example = "login@gmail.com")
        String username,
        @Schema(description = "User's first name", example = "Test")
        String name,
        @Schema(description = "User's last name", example = "Testov")
        String surname
) {
}
