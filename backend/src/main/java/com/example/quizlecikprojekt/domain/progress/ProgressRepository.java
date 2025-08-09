package com.example.quizlecikprojekt.domain.progress;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgressRepository extends JpaRepository<Progress, Long> {

  Optional<Progress> findByUserIdAndStudyDate(Long userId, LocalDate studyDate);

  List<Progress> findByUserIdOrderByStudyDateDesc(Long userId);

  List<Progress> findByUserIdAndStudyDateBetweenOrderByStudyDateDesc(
      Long userId, LocalDate startDate, LocalDate endDate);

  @Query(
      "SELECT p FROM Progress p WHERE p.user.id = :userId AND p.studyDate >= :startDate ORDER BY p.studyDate DESC")
  List<Progress> findRecentProgressByUserId(
      @Param("userId") Long userId, @Param("startDate") LocalDate startDate);

  @Query("SELECT SUM(p.totalWordsStudied) FROM Progress p WHERE p.user.id = :userId")
  Long getTotalWordsStudiedByUserId(@Param("userId") Long userId);

  @Query("SELECT SUM(p.studyTimeMinutes) FROM Progress p WHERE p.user.id = :userId")
  Long getTotalStudyTimeByUserId(@Param("userId") Long userId);

  @Query("SELECT AVG(p.accuracyPercentage) FROM Progress p WHERE p.user.id = :userId")
  Double getAverageAccuracyByUserId(@Param("userId") Long userId);

  @Query("SELECT COUNT(DISTINCT p.studyDate) FROM Progress p WHERE p.user.id = :userId")
  Long getTotalStudyDaysByUserId(@Param("userId") Long userId);

  @Query("SELECT p FROM Progress p WHERE p.user.id = :userId ORDER BY p.studyDate DESC LIMIT 1")
  Optional<Progress> findLastStudyDateByUserId(@Param("userId") Long userId);
}
