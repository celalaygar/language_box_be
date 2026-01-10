package com.game.find.word.ScrambledWord.dto;



import com.game.find.word.base.model.EnglishLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Response DTO for a single word")
public class WordResponseDto {

    @Schema(description = "Unique identifier of the word", example = "64f8a1234b5c6d7e8f9a0b1c")
    private String id;

    @Schema(description = "Word with shuffled letters", example = "apple")
    private String word;

    @Schema(description = "Word with shuffled letters", example = "paple")
    private String shuffledWord;

    @Schema(description = "English level of the word", example = "A1")
    private EnglishLevel level;

    @Schema(description = "created date of the word", example = "2025-09-01T23:56:56.007")
    private LocalDateTime createdAt;
}
