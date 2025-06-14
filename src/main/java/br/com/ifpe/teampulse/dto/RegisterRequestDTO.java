package br.com.ifpe.teampulse.dto;

import br.com.ifpe.teampulse.domain.user.UserType;

public record RegisterRequestDTO(
        String username,
        String email,
        String password,
        UserType userType
) {}
