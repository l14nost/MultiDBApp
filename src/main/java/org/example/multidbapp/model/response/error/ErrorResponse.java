package org.example.multidbapp.model.response.error;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response object containing basic error information")
public record ErrorResponse(
        @Schema(example = "field")
        String field,
        @Schema(example = "Field must not be null")
        String message
) {
}
