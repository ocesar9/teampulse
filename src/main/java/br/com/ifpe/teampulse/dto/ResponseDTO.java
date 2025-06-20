package br.com.ifpe.teampulse.dto;

import br.com.ifpe.teampulse.domain.user.UserType;

public record ResponseDTO(String id , String username, String email, UserType type, String token) {
}
