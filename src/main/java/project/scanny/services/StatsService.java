package project.scanny.services;

import project.scanny.dto.NeighborDTO;
import project.scanny.dto.StatsPerUserAndLanguageDTO;
import project.scanny.models.User;

import java.util.List;

public interface StatsService {
    List<StatsPerUserAndLanguageDTO> getStatsPerUserAndLanguages(User user);

    List<NeighborDTO> getNeighborsForLanguage(User user, String selectedLangCode);
}
