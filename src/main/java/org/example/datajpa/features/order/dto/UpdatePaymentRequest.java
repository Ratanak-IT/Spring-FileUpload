package org.example.datajpa.features.order.dto;
import jakarta.validation.constraints.NotNull;

public record UpdatePaymentRequest(
        @NotNull(message = "status is required")
        Boolean status
) {
}
