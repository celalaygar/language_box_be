package com.game.find.word.ScrambledWord.dto;


import com.game.find.word.base.model.EnglishLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Response DTO containing all English levels")
public class EnglishLevelResponseDto {

    @Schema(description = "List of English proficiency levels", example = "[\"A1\", \"A2\", \"B1\", \"B2\", \"C1\", \"C2\"]")
    private List<EnglishLevel> levels;
}
