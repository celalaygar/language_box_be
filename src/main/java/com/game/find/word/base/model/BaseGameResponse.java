package com.game.find.word.base.model;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaseGameResponse<T> {

    private EnglishLevel level;
    private Long size;
    private Language language;
    private List<T> list;
}
