package live.switchly.api.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateFlagDescriptionRequest(
        @NotBlank
        String description
) {}