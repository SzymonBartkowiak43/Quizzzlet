package com.example.quizlecikprojekt.newweb.dto.video;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideosListResponse {
  private List<VideoSummaryResponse> videos;
  private List<VideoSummaryResponse> topRatedVideos;
  private int totalVideos;
}
