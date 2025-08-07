package org.example.multidbapp.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NonNull;

@Schema(description = "Standard wrapper for API responses containing operation status and optional data.")
public record GlobalResponse<T>(
        @NonNull
        @Schema(description = "Indicates whether the operation was successful.<b> 'true' means success, 'false' means failure.", example = "true")
        Boolean isSuccess,
        @Schema(description = "Returned data in case of a successful operation.<b> Can be null if no data is available.")
        T data
) {
}
