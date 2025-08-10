package com.example.quizlecikprojekt.domain.progress;

import com.example.quizlecikprojekt.domain.progress.dto.*;
import com.example.quizlecikprojekt.domain.user.User;
import com.example.quizlecikprojekt.domain.user.UserService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ProgressFacade {

  private static final Logger logger = LoggerFactory.getLogger(ProgressFacade.class);

  private final ProgressService progressService;
  private final UserService userService;

  public ProgressFacade(ProgressService progressService, UserService userService) {
    this.progressService = progressService;
    this.userService = userService;
  }

  public DailyProgressResponse recordStudySession(
      String userEmail, RecordStudySessionRequest request) {
    logger.info("Recording study session for user: {}", userEmail);

    User user = userService.getUserByEmail(userEmail);
    Progress progress = progressService.recordStudySession(user, request);

    return mapToDailyProgressResponse(progress);
  }

  public ProgressSummaryResponse getProgressSummary(String userEmail) {
    logger.info("Getting progress summary for user: {}", userEmail);

    User user = userService.getUserByEmail(userEmail);
    return progressService.getProgressSummary(user);
  }

  public List<WeeklyProgressResponse> getWeeklyProgress(String userEmail, int weeks) {
    logger.info("Getting weekly progress for user: {} (last {} weeks)", userEmail, weeks);

    User user = userService.getUserByEmail(userEmail);
    return progressService.getWeeklyProgress(user, weeks);
  }

  public ProgressStatsResponse getProgressStats(String userEmail) {
    logger.info("Getting progress stats for user: {}", userEmail);

    User user = userService.getUserByEmail(userEmail);
    return progressService.getProgressStats(user);
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
}
