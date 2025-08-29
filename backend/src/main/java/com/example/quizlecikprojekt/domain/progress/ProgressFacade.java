package com.example.quizlecikprojekt.domain.progress;

import com.example.quizlecikprojekt.domain.progress.dto.*;
import com.example.quizlecikprojekt.domain.user.User;
import com.example.quizlecikprojekt.domain.user.UserService;
import java.util.List;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ProgressFacade {

  private final ProgressService progressService;
  private final UserService userService;

  public DailyProgressResponse recordStudySession(
          String userEmail, RecordStudySessionRequest request) {

    User user = userService.getUserByEmail(userEmail);
    Progress progress = progressService.recordStudySession(user, request);

    return mapToDailyProgressResponse(progress);
  }

  public ProgressSummaryResponse getProgressSummary(String userEmail) {
    User user = userService.getUserByEmail(userEmail);
    return progressService.getProgressSummary(user);
  }

  public List<WeeklyProgressResponse> getWeeklyProgress(String userEmail, int weeks) {
    User user = userService.getUserByEmail(userEmail);
    return progressService.getWeeklyProgress(user, weeks);
  }

  public ProgressStatsResponse getProgressStats(String userEmail) {
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
