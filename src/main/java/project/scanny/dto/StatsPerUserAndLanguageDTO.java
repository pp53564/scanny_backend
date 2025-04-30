package project.scanny.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatsPerUserAndLanguageDTO {
    private String languageCode;
    private long correctAnswers;
    private long attemptSum;
    private double score;
    private int rank;
    private int totalUsersInLang;
    private long totalQuestions;

    public StatsPerUserAndLanguageDTO(String languageCode, long correctAnswers,
                                      long attemptSum, double score,
                                      int rank, int totalUsersInLang, long totalQuestions) {
        this.languageCode = languageCode;
        this.correctAnswers = correctAnswers;
        this.attemptSum = attemptSum;
        this.score = score;
        this.rank = rank;
        this.totalUsersInLang = totalUsersInLang;
        this.totalQuestions = totalQuestions;
    }
}
