package com.game.find.word.version.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "app_version")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppVersion {

    @Id
    private String id;
    private Date createdDate;
    private Boolean control;
    private String versionNumber;
}
