package com.game.find.word.ScrambledWord.model;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Word {
    private String id;
    private String word;
    private String shuffledWord;
    private String hint;
}
