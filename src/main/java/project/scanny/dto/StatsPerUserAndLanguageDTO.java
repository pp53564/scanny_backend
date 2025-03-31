package project.scanny.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
public class StatsPerUserAndLanguageDTO {

    private final String language;
    private final Long totalQuestions;
    private final Long answered;
    private final Long correct;
    private final Double accuracy;
    private final Double avgAttemptsPerQuestion;
    private final Double avgAttemptsForCorrect;

    public StatsPerUserAndLanguageDTO(
            String language,
            Long totalQuestions,
            Long answered,
            Long correct,
            Long rawCorrectCount,
            Long totalAttemptCount,
            Double avgAttemptsPerQuestion,
            Double avgAttemptsForCorrect
    ) {
        this.language = language;
        this.totalQuestions = totalQuestions;
        this.answered = answered;
        this.correct = correct;

        if (rawCorrectCount != null && totalAttemptCount != null && totalAttemptCount > 0) {
            this.accuracy = round((rawCorrectCount * 100.0) / totalAttemptCount);
        } else {
            this.accuracy = 0.0;
        }

        this.avgAttemptsPerQuestion = avgAttemptsPerQuestion != null ? round(avgAttemptsPerQuestion) : 0.0;
        this.avgAttemptsForCorrect = avgAttemptsForCorrect != null ? round(avgAttemptsForCorrect) : 0.0;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

}


