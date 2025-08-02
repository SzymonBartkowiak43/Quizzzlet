package com.example.quizlecikprojekt.domain.user.dto;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {
  private Long id;
  private String email;
  private String userName;
  private Set<String> roles;
}
