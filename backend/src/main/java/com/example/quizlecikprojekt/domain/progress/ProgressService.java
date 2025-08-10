package com.example.quizlecikprojekt.domain.progress;

import com.example.quizlecikprojekt.domain.progress.dto.*;
import com.example.quizlecikprojekt.domain.user.User;
import com.example.quizlecikprojekt.domain.wordset.WordSetFacade;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ProgressService {

  private final ProgressRepository progressRepository;
  private final WordSetFacade wordSetService;

  public Progress recordStudySession(User user, RecordStudySessionRequest request) {

    LocalDate today = LocalDate.now();
    Optional<Progress> existingProgress =
        progressRepository.findByUserIdAndStudyDate(user.getId(), today);

    Progress progress;
    if (existingProgress.isPresent()) {
      progress = existingProgress.get();
      progress.setTotalWordsStudied(progress.getTotalWordsStudied() + request.totalWordsStudied());
      progress.setCorrectAnswers(progress.getCorrectAnswers() + request.correctAnswers());
      progress.setIncorrectAnswers(progress.getIncorrectAnswers() + request.incorrectAnswers());
      progress.setFlashcardsCompleted(
          progress.getFlashcardsCompleted()
              + Optional.ofNullable(request.flashcardsCompleted()).orElse(0));
      progress.setQuizzesCompleted(
          progress.getQuizzesCompleted()
              + Optional.ofNullable(request.quizzesCompleted()).orElse(0));
      progress.setStudyTimeMinutes(
          progress.getStudyTimeMinutes()
              + Optional.ofNullable(request.studyTimeMinutes()).orElse(0));
    } else {
      progress = new Progress();
      progress.setUser(user);
      if (request.wordSetId() != null) {
        progress.setWordSet(wordSetService.getWordSetById(request.wordSetId()));
      }
      progress.setStudyDate(today);
      progress.setTotalWordsStudied(request.totalWordsStudied());
      progress.setCorrectAnswers(request.correctAnswers());
      progress.setIncorrectAnswers(request.incorrectAnswers());
      progress.setFlashcardsCompleted(Optional.ofNullable(request.flashcardsCompleted()).orElse(0));
      progress.setQuizzesCompleted(Optional.ofNullable(request.quizzesCompleted()).orElse(0));
      progress.setStudyTimeMinutes(Optional.ofNullable(request.studyTimeMinutes()).orElse(0));
    }

    progress.setStreakCount(getCurrentStreak(user.getId()));

    return progressRepository.save(progress);
  }

  public ProgressSummaryResponse getProgressSummary(User user) {
    List<Progress> recentProgress =
        progressRepository.findRecentProgressByUserId(user.getId(), LocalDate.now().minusDays(30));

    Long totalWordsStudied = progressRepository.getTotalWordsStudiedByUserId(user.getId());
    Long totalStudyTime = progressRepository.getTotalStudyTimeByUserId(user.getId());
    Double overallAccuracy = progressRepository.getAverageAccuracyByUserId(user.getId());

    int currentStreak = getCurrentStreak(user.getId());
    int longestStreak = getLongestStreak(user.getId());

    Optional<Progress> lastProgress = progressRepository.findLastStudyDateByUserId(user.getId());
    LocalDate lastStudyDate = lastProgress.map(Progress::getStudyDate).orElse(null);

    int totalFlashcards = recentProgress.stream().mapToInt(Progress::getFlashcardsCompleted).sum();
    int totalQuizzes = recentProgress.stream().mapToInt(Progress::getQuizzesCompleted).sum();

    List<DailyProgressResponse> dailyProgress =
        recentProgress.stream().map(this::mapToDailyProgressResponse).collect(Collectors.toList());

    return new ProgressSummaryResponse(
        user.getId(),
        user.getName(),
        currentStreak,
        longestStreak,
        totalWordsStudied != null ? totalWordsStudied.intValue() : 0,
        totalFlashcards,
        totalQuizzes,
        totalStudyTime != null ? totalStudyTime.intValue() : 0,
        overallAccuracy != null ? overallAccuracy : 0.0,
        lastStudyDate,
        dailyProgress);
  }

  public List<WeeklyProgressResponse> getWeeklyProgress(User user, int weeks) {
    LocalDate endDate = LocalDate.now();
    LocalDate startDate = endDate.minusWeeks(weeks);

    List<Progress> progressList =
        progressRepository.findByUserIdAndStudyDateBetweenOrderByStudyDateDesc(
            user.getId(), startDate, endDate);

    return groupProgressByWeek(progressList);
  }

  public ProgressStatsResponse getProgressStats(User user) {
    Long totalDaysStudied = progressRepository.getTotalStudyDaysByUserId(user.getId());
    int currentStreak = getCurrentStreak(user.getId());
    int longestStreak = getLongestStreak(user.getId());

    List<WeeklyProgressResponse> weeklyProgress = getWeeklyProgress(user, 12); // Last 12 weeks
    List<MonthlyProgressResponse> monthlyProgress = getMonthlyProgress(user, 6); // Last 6 months

    return new ProgressStatsResponse(
        totalDaysStudied != null ? totalDaysStudied.intValue() : 0,
        currentStreak,
        longestStreak,
        getMostActiveDay(user.getId()),
        "Morning", // This could be calculated based on session times
        getMostStudiedWordSet(user.getId()),
        weeklyProgress,
        monthlyProgress);
  }

  private int getCurrentStreak(Long userId) {
    List<Progress> recentProgress = progressRepository.findByUserIdOrderByStudyDateDesc(userId);
    if (recentProgress.isEmpty()) {
      return 0;
    }

    int streak = 0;
    LocalDate today = LocalDate.now();
    LocalDate checkDate = today;

    for (Progress progress : recentProgress) {
      if (progress.getStudyDate().equals(checkDate)
          || (streak == 0 && progress.getStudyDate().equals(today.minusDays(1)))) {
        streak++;
        checkDate = progress.getStudyDate().minusDays(1);
      } else {
        break;
      }
    }

    return streak;
  }

  private int getLongestStreak(Long userId) {
    List<Progress> allProgress = progressRepository.findByUserIdOrderByStudyDateDesc(userId);
    if (allProgress.isEmpty()) {
      return 0;
    }

    int longestStreak = 0;
    int currentStreak = 1;

    for (int i = 1; i < allProgress.size(); i++) {
      Progress current = allProgress.get(i);
      Progress previous = allProgress.get(i - 1);

      long daysBetween = ChronoUnit.DAYS.between(current.getStudyDate(), previous.getStudyDate());

      if (daysBetween == 1) {
        currentStreak++;
      } else {
        longestStreak = Math.max(longestStreak, currentStreak);
        currentStreak = 1;
      }
    }

    return Math.max(longestStreak, currentStreak);
  }

  private DailyProgressResponse mapToDailyProgressResponse(Progress progress) {
    return new DailyProgressResponse(
        progress.getStudyDate(),
        progress.getTotalWordsStudied(),
        progress.getCorrectAnswers(),
        progress.getIncorrectAnswers(),
        progress.getFlashcardsCompleted(),
        progress.getQuizzesCompleted(),
        progress.getStudyTimeMinutes(),
        progress.getAccuracyPercentage());
  }

  private List<WeeklyProgressResponse> groupProgressByWeek(List<Progress> progressList) {
    // Implementation to group progress by week
    return List.of(); // Simplified for now
  }

  private List<MonthlyProgressResponse> getMonthlyProgress(User user, int months) {
    // Implementation to get monthly progress
    return List.of(); // Simplified for now
  }

  private String getMostActiveDay(Long userId) {
    // Implementation to find most active day of the week
    return "Monday"; // Simplified for now
  }

  private WordSetProgressResponse getMostStudiedWordSet(Long userId) {
    // Implementation to find most studied word set
    return null; // Simplified for now
  }
}
