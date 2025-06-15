package com.example.quizlecikprojekt.newweb;

import com.example.quizlecikprojekt.deeplytranzlator.WordsTranslator;
import com.example.quizlecikprojekt.newweb.dto.ApiResponse;
import com.example.quizlecikprojekt.newweb.dto.tranzlation.TranslationRequest;
import com.example.quizlecikprojekt.newweb.dto.tranzlation.TranslationResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/translation")
@CrossOrigin(origins = "http://localhost:3000")
public class TranslationRestController {

    private static final Logger logger = LoggerFactory.getLogger(TranslationRestController.class);

    private final WordsTranslator wordsTranslator;

    public TranslationRestController(WordsTranslator wordsTranslator) {
        this.wordsTranslator = wordsTranslator;
    }

    @PostMapping("/translate")
    public ResponseEntity<ApiResponse<TranslationResponse>> translate(
            @Valid @RequestBody TranslationRequest request,
            Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not authenticated"));
            }

            String userEmail = authentication.getName();

            if (request.text() == null || request.text().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Text to translate cannot be empty"));
            }

            if (request.sourceLanguage() == null || request.targetLanguage() == null) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Source and target languages are required"));
            }

            if (request.sourceLanguage().equals(request.targetLanguage())) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Source and target languages cannot be the same"));
            }

            String text = request.text().trim();
            String sourceLanguage = request.sourceLanguage().toLowerCase();
            String targetLanguage = request.targetLanguage().toLowerCase();

            logger.debug("Translation request from user: {} - '{}' from {} to {}",
                    userEmail, text, sourceLanguage, targetLanguage);

            String translation = wordsTranslator.translate(text, sourceLanguage, targetLanguage);

            TranslationResponse response = new TranslationResponse();
            response.setOriginalText(text);
            response.setSourceLanguage(sourceLanguage);
            response.setTargetLanguage(targetLanguage);

            if (translation.equals(text)) {
                response.setTranslatedText("");
                response.setTranslationSuccessful(false);
                response.setMessage("Translation not available or text unchanged");

                logger.debug("Translation failed or unchanged for user: {} - text: '{}'", userEmail, text);
                return ResponseEntity.ok(ApiResponse.success("Translation completed", response));
            } else {
                response.setTranslatedText(translation);
                response.setTranslationSuccessful(true);
                response.setMessage("Translation successful");

                logger.info("Translation successful for user: {} - '{}' -> '{}'", userEmail, text, translation);
                return ResponseEntity.ok(ApiResponse.success("Translation successful", response));
            }

        } catch (Exception e) {
            logger.error("Error during translation for user: {} - text: '{}'",
                    authentication != null ? authentication.getName() : "unknown",
                    request != null ? request.text() : "null", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Translation service temporarily unavailable"));
        }
    }
}