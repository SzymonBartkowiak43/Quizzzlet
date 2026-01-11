package pl.bartkowiak.quizlecikprojekt.domain.progress;

import com.example.quizlecikprojekt.domain.progress.dto.*;
import pl.bartkowiak.quizlecikprojekt.domain.user.UserFacade;
import pl.bartkowiak.quizlecikprojekt.entity.User;
import java.util.List;

import pl.bartkowiak.quizlecikprojekt.entity.Progress;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pl.bartkowiak.quizlecikprojekt.domain.progress.dto.*;

@Component
@AllArgsConstructor
public class ProgressFacade {

  private final ProgressService progressService;
  private final UserFacade userFacade;

  public DailyProgressResponse recordStudySession(
          String userEmail, RecordStudySessionRequest request) {

    User user = userFacade.getUserByEmail(userEmail);
    Progress progress = progressService.recordStudySession(user, request);

    return mapToDailyProgressResponse(progress);
  }

  public ProgressSummaryResponse getProgressSummary(String userEmail) {
    User user = userFacade.getUserByEmail(userEmail);
    return progressService.getProgressSummary(user);
  }

  public List<WeeklyProgressResponse> getWeeklyProgress(String userEmail, int weeks) {
    User user = userFacade.getUserByEmail(userEmail);
    return progressService.getWeeklyProgress(user, weeks);
  }

  public ProgressStatsResponse getProgressStats(String userEmail) {
    User user = userFacade.getUserByEmail(userEmail);
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
