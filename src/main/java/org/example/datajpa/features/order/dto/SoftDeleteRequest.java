package org.example.datajpa.features.order.dto;
import jakarta.validation.constraints.NotNull;

public record SoftDeleteRequest(
        @NotNull(message = "status is required")
        Boolean isDelete
) {
}
