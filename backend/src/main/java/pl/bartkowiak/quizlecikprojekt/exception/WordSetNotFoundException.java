package pl.bartkowiak.quizlecikprojekt.exception;

public class WordSetNotFoundException extends RuntimeException {
  public WordSetNotFoundException(String message) {
    super(message);
  }
}
