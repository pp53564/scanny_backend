package project.scanny.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Base64;

@Getter
public class AnsweredQuestionDTO {

    private final String text;

    @JsonProperty("imageBase64")
    private final String imageBase64;

    public AnsweredQuestionDTO(String text, byte[] imageBytes) {
        this.text = text;
        this.imageBase64 = Base64.getEncoder().encodeToString(imageBytes);
    }

}
