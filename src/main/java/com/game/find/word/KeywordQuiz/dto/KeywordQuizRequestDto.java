package com.game.find.word.KeywordQuiz.dto;


import com.game.find.word.base.model.EnglishLevel;
import com.game.find.word.base.model.Language;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Request DTO for paginated word query")
public class KeywordQuizRequestDto {

    @Schema(description = "Page number (0-based)", example = "0")
    private int page = 0;

    @Schema(description = "Number of words per page", example = "20")
    private int size = 20;

    @Schema(description = "Filter by English level", example = "B1")
    private EnglishLevel level;

    @Schema(description = "Filter by English level", example = "EN")
    private Language language;

    @Schema(description = "count", example = "20")
    private Integer count;
}
