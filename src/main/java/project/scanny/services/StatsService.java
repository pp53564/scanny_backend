package project.scanny.services;

import org.springframework.data.repository.query.Param;
import project.scanny.dto.StatsPerUserAndLanguageDTO;

import java.util.List;

public interface StatsService {
    List<StatsPerUserAndLanguageDTO> getStatsPerUserAndLanguages(Long userId);
}
