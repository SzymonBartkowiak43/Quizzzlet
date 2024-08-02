package com.example.quizlecikprojekt.comment.Dto;


import com.example.quizlecikprojekt.deeplyTranzlator.DateTimeFormatterUtil;
import com.example.quizlecikprojekt.user.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentDto {
    private Long id;
    private String content;
    private User user;
    private LocalDateTime dateAndTime;

    public String getFormattedDateAndTime() {
        return DateTimeFormatterUtil.format(dateAndTime);
    }
}
