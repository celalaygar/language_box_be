package com.game.find.word.GridChallenge.entity;


import lombok.Data;

@Data
public class WordPlacement {
    private String word;
    private Integer startRow;
    private Integer startCol;
    private Integer endRow;
    private Integer endCol;
    private String direction;
}
