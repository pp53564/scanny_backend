package project.scanny.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import project.scanny.dao.UserQuestionAttemptRepository;
import project.scanny.dto.StatsPerUserAndLanguageDTO;
import project.scanny.services.StatsService;

import java.util.List;

@Service
@Slf4j
public class StatsServiceImpl implements StatsService {
    private final UserQuestionAttemptRepository userQuestionAttemptRepository;

    public StatsServiceImpl(UserQuestionAttemptRepository userQuestionAttemptRepository) {
        this.userQuestionAttemptRepository = userQuestionAttemptRepository;
    }

    public List<StatsPerUserAndLanguageDTO> getStatsPerUserAndLanguages(Long userId) {
        return userQuestionAttemptRepository.getStatsPerUserAndLanguages(userId);
    }
}
