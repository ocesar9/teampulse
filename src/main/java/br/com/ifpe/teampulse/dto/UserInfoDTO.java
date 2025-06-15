package br.com.ifpe.teampulse.dto;

import br.com.ifpe.teampulse.domain.user.UserType;

public record UserInfoDTO(String username, UserType userType) {}
