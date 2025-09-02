package backend.ASP.dto;

import backend.ASP.entity.user.UserRole;

public record RegisterDTO(String login, String password, UserRole role) {
}
