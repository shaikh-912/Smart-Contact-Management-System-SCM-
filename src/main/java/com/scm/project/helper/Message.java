package com.scm.project.helper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    private String content;
    @Builder.Default
    private MessageType type=MessageType.blue;
}
