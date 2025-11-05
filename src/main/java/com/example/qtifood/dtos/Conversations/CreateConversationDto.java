package com.example.qtifood.dtos.Conversations;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateConversationDto {
    
    @NotNull(message = "Customer ID is required")
    private String customerId;
    
    @NotNull(message = "Seller ID is required")
    private String sellerId;
}