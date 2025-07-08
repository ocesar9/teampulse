package br.com.ifpe.teampulse.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedbackRequest {
    private String userId;
    private String comment;
    private int rating;
}
