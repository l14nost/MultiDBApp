package org.example.multidbapp.model.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserFilterRequest(
        @NotBlank(message = "Filter must be specified")
        @Size(max = 100, message = "Filter length must be at most 100 characters")
        String filter
) {
}
