package com.everis.springboot.credits.documents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientTypeDocument {
    @Id
    private String id;

    private String description;
}
