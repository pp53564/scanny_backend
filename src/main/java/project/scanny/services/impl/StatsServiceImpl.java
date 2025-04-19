package project.scanny.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import project.scanny.dao.UserQuestionAttemptRepository;
import project.scanny.dao.UserRepository;
import project.scanny.dto.NeighborDTO;
import project.scanny.dto.StatsPerUserAndLanguageDTO;
import project.scanny.models.User;
import project.scanny.models.UserQuestionAttempt;
import project.scanny.services.StatsService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StatsServiceImpl implements StatsService {

    private final UserQuestionAttemptRepository userQuestionAttemptRepository;
    private final UserRepository userRepository;

    public StatsServiceImpl(UserQuestionAttemptRepository userQuestionAttemptRepository, UserRepository userRepository) {
        this.userQuestionAttemptRepository = userQuestionAttemptRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<StatsPerUserAndLanguageDTO> getStatsPerUserAndLanguages(User user) {
        List<UserQuestionAttempt> allAttempts = userQuestionAttemptRepository.findAll();
        if (allAttempts.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, List<UserQuestionAttempt>> attemptsByLanguage = allAttempts.stream()
                .collect(Collectors.groupingBy(UserQuestionAttempt::getLanguageCode));

        List<StatsPerUserAndLanguageDTO> result = new ArrayList<>();

        for (String languageCode : attemptsByLanguage.keySet()) {
            List<UserStats> rankedStats = computeRankedStatsForLanguage(languageCode);
            if (rankedStats.isEmpty()) {
                continue;
            }

            UserStats userStat = rankedStats.stream()
                    .filter(s -> s.userId.equals(user.getId()))
                    .findFirst()
                    .orElse(null);

            if (userStat != null) {
                StatsPerUserAndLanguageDTO dto = new StatsPerUserAndLanguageDTO(
                        userStat.languageCode,
                        userStat.correctAnswers,
                        userStat.attemptSum,
                        userStat.score,
                        userStat.rank,
                        rankedStats.size()
                );
                result.add(dto);
            }
        }

        return result;
    }

    @Override
    public List<NeighborDTO> getNeighborsForLanguage(User user, String selectedLangCode) {
        List<UserStats> rankedStats = computeRankedStatsForLanguage(selectedLangCode);
        if (rankedStats.isEmpty()) {
            return Collections.emptyList();
        }

        UserStats currentUserStat = rankedStats.stream()
                .filter(s -> s.userId.equals(user.getId()))
                .findFirst()
                .orElse(null);

        if (currentUserStat == null) {
            return Collections.emptyList();
        }

        int currentIndex = rankedStats.indexOf(currentUserStat);
        if (currentIndex == rankedStats.size() - 1) {
            Random random = new Random();
            double ivanDiff = 1.0 + (random.nextDouble() * 4.0);
            double lucijaDiff = 1.0 + (random.nextDouble() * 4.0);

            double ivanScore = currentUserStat.score - ivanDiff;
            double lucijaScore = currentUserStat.score - lucijaDiff;

            UserStats ivan = new UserStats(-101L, selectedLangCode, 0, 0, ivanScore < 0 ? 0 : ivanScore);
            UserStats lucija = new UserStats(-102L, selectedLangCode, 0, 0, lucijaScore < 0 ? 0 : lucijaScore);

            rankedStats.add(ivan);
            rankedStats.add(lucija);

            customSort(rankedStats);
            currentIndex = rankedStats.indexOf(currentUserStat);
        }
        int fromIndex = Math.max(0, currentIndex - 2);
        int toIndex = Math.min(rankedStats.size(), currentIndex + 3);
        List<UserStats> slice = rankedStats.subList(fromIndex, toIndex);

        return slice.stream()
                .map(s -> {
                    if (s.userId < 0) {
                        if (s.userId == -101L) {
                            return new NeighborDTO(s.userId, "ivan", s.score, s.rank);
                        } else {
                            return new NeighborDTO(s.userId, "lucija", s.score, s.rank);
                        }
                    }
                    Optional<User> optionalUser = userRepository.findById(s.userId);
                    if (optionalUser.isEmpty()) {
                        log.warn("No user found for userId: {}", s.userId);
                    }
                    String username = optionalUser.map(User::getUsername).orElse("Unknown");
                    return new NeighborDTO(s.userId, username, s.score, s.rank);
                }).collect(Collectors.toList());
    }

    private void customSort(List<UserStats> rankedStats) {
        rankedStats.sort((a, b) -> Double.compare(b.score, a.score));

        double prevScore = Double.NaN;
        int rank = 0;
        for (int i = 0; i < rankedStats.size(); i++) {
            UserStats current = rankedStats.get(i);
            if (i == 0 || Math.abs(current.score - prevScore) > 1e-9) {
                rank = i + 1;
                prevScore = current.score;
            }
            current.rank = rank;
        }
    }

    private List<UserStats> computeRankedStatsForLanguage(String languageCode) {
        List<UserQuestionAttempt> attemptsInLang =
                userQuestionAttemptRepository.findAllByLanguageCode(languageCode);
        if (attemptsInLang.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, List<UserQuestionAttempt>> attemptsByUser = attemptsInLang.stream()
                .collect(Collectors.groupingBy(ua -> ua.getUser().getId()));

        List<UserStats> statsList = attemptsByUser.entrySet().stream()
                .map(e -> computeUserStats(e.getKey(), e.getValue(), languageCode))
                .collect(Collectors.toList());

        if (statsList.isEmpty()) {
            return Collections.emptyList();
        }

        customSort(statsList);

        return statsList;
    }

    private UserStats computeUserStats(Long userId, List<UserQuestionAttempt> attempts, String languageCode) {
        long correctAnswers = 0;
        long attemptSum = 0;
        double score = 0.0;

//        for (UserQuestionAttempt ua : attempts) {
//            attemptSum += ua.getAttemptCount();
//            if (ua.isSucceeded()) {
//                correctAnswers++;
//                score += 2;
//                score -= (ua.getAttemptCount() - 1);
//            } else {
//                score -= ua.getAttemptCount();
//            }
//        }
//
//        int score = 0;
//        int correctAnswers = 0;
//        int attemptSum = 0;
//
//// You can tweak these values as you like:
        final int BASE_SCORE = 10;
        final int PENALTY_PER_EXTRA_ATTEMPT = 2;
        final int FAILURE_PENALTY = 2;

        for (UserQuestionAttempt ua : attempts) {
            int attemptsForThisQuestion = ua.getAttemptCount();
            attemptSum += attemptsForThisQuestion;

            if (ua.isSucceeded()) {
                correctAnswers++;
                int gained = BASE_SCORE - PENALTY_PER_EXTRA_ATTEMPT * (attemptsForThisQuestion - 1);

                if (gained < 0) {
                    gained = 0;
                }

                score += gained;
            } else {
                score -= FAILURE_PENALTY;
            }
        }


        return new UserStats(userId, languageCode, correctAnswers, attemptSum, score < 0 ? 0 : score);
    }

    static class UserStats {
        final Long userId;
        final String languageCode;
        final long correctAnswers;
        final long attemptSum;
        final double score;
        int rank;

        public UserStats(Long userId, String languageCode,
                         long correctAnswers, long attemptSum, double score) {
            this.userId = userId;
            this.languageCode = languageCode;
            this.correctAnswers = correctAnswers;
            this.attemptSum = attemptSum;
            this.score = score;
        }
    }
}
