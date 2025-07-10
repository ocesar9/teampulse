package br.com.ifpe.teampulse.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedbackUpdateRequest {
    private String comment;
    private int rating;
}