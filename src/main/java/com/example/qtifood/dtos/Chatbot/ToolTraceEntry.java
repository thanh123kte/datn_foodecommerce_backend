package com.example.qtifood.dtos.Chatbot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToolTraceEntry {
    private String tool;
    private Map<String, Object> args;
    private Integer count;
}
