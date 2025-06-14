package br.com.ifpe.teampulse.dto;

import br.com.ifpe.teampulse.domain.user.UserType;

public record ResponseDTO(String name, String token, UserType type) {
}
