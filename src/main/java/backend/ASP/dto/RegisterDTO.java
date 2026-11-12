package backend.ASP.dto;

import backend.ASP.entity.user.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterDTO(
        @NotBlank(message = "O login é obrigatório") String login,
        @NotBlank(message = "A senha é obrigatória")
        @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres") String password,
        @NotNull(message = "O perfil é obrigatório") UserRole role) {
}