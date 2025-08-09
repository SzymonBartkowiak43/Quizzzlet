package com.example.quizlecikprojekt.domain.progress;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceEvaluationRepository extends JpaRepository<ResourceEvaluation, Long> {

  Optional<ResourceEvaluation> findByUserIdAndWordSetId(Long userId, Long wordSetId);

  Optional<ResourceEvaluation> findByUserIdAndVideoId(Long userId, Long videoId);

  List<ResourceEvaluation> findByWordSetIdOrderByCreatedAtDesc(Long wordSetId);

  List<ResourceEvaluation> findByVideoIdOrderByCreatedAtDesc(Long videoId);

  List<ResourceEvaluation> findByUserIdOrderByCreatedAtDesc(Long userId);

  @Query("SELECT AVG(e.rating) FROM ResourceEvaluation e WHERE e.wordSet.id = :wordSetId")
  Double getAverageRatingForWordSet(@Param("wordSetId") Long wordSetId);

  @Query("SELECT AVG(e.rating) FROM ResourceEvaluation e WHERE e.video.id = :videoId")
  Double getAverageRatingForVideo(@Param("videoId") Long videoId);

  @Query("SELECT AVG(e.usefulnessRating) FROM ResourceEvaluation e WHERE e.wordSet.id = :wordSetId")
  Double getAverageUsefulnessForWordSet(@Param("wordSetId") Long wordSetId);

  @Query(
      "SELECT COUNT(e) FROM ResourceEvaluation e WHERE e.wordSet.id = :wordSetId AND e.wouldRecommend = true")
  Long getRecommendationCountForWordSet(@Param("wordSetId") Long wordSetId);

  @Query("SELECT COUNT(e) FROM ResourceEvaluation e WHERE e.wordSet.id = :wordSetId")
  Long getTotalEvaluationsForWordSet(@Param("wordSetId") Long wordSetId);

  @Query(
      "SELECT e.difficultyLevel, COUNT(e) FROM ResourceEvaluation e WHERE e.wordSet.id = :wordSetId GROUP BY e.difficultyLevel ORDER BY COUNT(e) DESC")
  List<Object[]> getDifficultyDistributionForWordSet(@Param("wordSetId") Long wordSetId);

  List<ResourceEvaluation> findTop10ByOrderByCreatedAtDesc();

  @Query("SELECT e FROM ResourceEvaluation e WHERE e.rating >= 4 ORDER BY e.createdAt DESC")
  List<ResourceEvaluation> findHighlyRatedResources();
}
