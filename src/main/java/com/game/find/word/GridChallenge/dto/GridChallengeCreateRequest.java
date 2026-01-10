package com.game.find.word.GridChallenge.dto;


import com.game.find.word.GridChallenge.entity.Settings;
import com.game.find.word.GridChallenge.entity.WordPlacement;
import com.game.find.word.base.model.EnglishLevel;
import com.game.find.word.base.model.Language;
import lombok.Data;

import java.util.List;

@Data
public class GridChallengeCreateRequest {
    private Settings settings;
    private List<List<String>> grid;
    private List<WordPlacement> words;
    private List<String> wordList;
    private EnglishLevel level;
    private Language language;
}
