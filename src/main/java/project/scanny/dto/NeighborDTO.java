package project.scanny.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NeighborDTO {
    private Long userId;
    private String username;
    private double score;
    private int rank;

    public NeighborDTO(Long userId, String username, double score, int rank) {
        this.userId = userId;
        this.username = username;
        this.score = score;
        this.rank = rank;
    }
}

