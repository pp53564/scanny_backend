package project.scanny.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AttemptResponse {
        private boolean correct;
        private float confidenceScore;
        private String message;
        private String matchedLabel;

        public AttemptResponse(boolean correct, float confidenceScore, String message, String matchedLabel) {
            this.correct = correct;
            this.confidenceScore = confidenceScore;
            this.message = message;
            this.matchedLabel = matchedLabel;
        }
}
