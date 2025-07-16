package br.com.ifpe.teampulse.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedbackSendRequest {
    @NotBlank(message = "O ID do feedback é obrigatório")
    private String feedbackId;
}
