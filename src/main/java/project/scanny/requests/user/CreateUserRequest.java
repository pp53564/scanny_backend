package project.scanny.requests.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateUserRequest(@NotNull @NotBlank String username, @NotNull @NotBlank String password) {
}
