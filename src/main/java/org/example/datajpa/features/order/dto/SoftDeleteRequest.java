package org.example.datajpa.features.order.dto;
import jakarta.validation.constraints.NotNull;

public record SoftDeleteRequest(
        @NotNull(message = "delete is required")
        Boolean isDelete
) {
}
