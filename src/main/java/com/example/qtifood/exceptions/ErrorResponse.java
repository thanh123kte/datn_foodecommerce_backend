package com.example.qtifood.exceptions;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(hidden = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

private int status;              
    private String error;            
    private List<String> messages;   
    private String path;             
    private LocalDateTime timestamp;
    
}
