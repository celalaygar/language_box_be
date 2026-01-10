package com.game.find.word.googleAI.dto.deepseek;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeepseekRequestPayload {
    private String model;
    private List<Message> messages;
}