package br.com.ifpe.teampulse.dto;

import br.com.ifpe.teampulse.domain.user.UserType;

public record ResponseDTO(String username, String email, String token, UserType type) {
}
