package com.game.find.word.ScrambledWord.dto;


import com.game.find.word.base.model.EnglishLevel;
import com.game.find.word.base.model.Language;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Request DTO for paginated word query")
public class WordPageRequestDto {

    @Schema(description = "Filter by English level", example = "B1")
    private EnglishLevel level;

    @Schema(description = "Filter by English level", example = "EN")
    private Language language;

    @Schema(description = "count", example = "20")
    private Integer count;

    Set<String> wordsList;
    private Long sequenceNumber;
}
