package com.example.qtifood.services.chatbot;

import com.google.genai.types.FunctionDeclaration;
import com.google.genai.types.Schema;
import com.google.genai.types.Tool;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ToolRegistry {

    @Getter
    private final List<Tool> tools;

    public ToolRegistry() {
        List<FunctionDeclaration> declarations = List.of(
                functionFromJson("""
                        {
                          "name": "searchStores",
                          "description": "Search stores by free text query",
                          "parameters": {
                            "type": "object",
                            "properties": { "query": { "type": "string" } },
                            "required": ["query"]
                          }
                        }
                        """),
                functionFromJson("""
                        {
                          "name": "getStoreById",
                          "description": "Get a store by id",
                          "parameters": {
                            "type": "object",
                            "properties": { "storeId": { "type": "integer" } },
                            "required": ["storeId"]
                          }
                        }
                        """),
                functionFromJson("""
                        {
                          "name": "searchProducts",
                          "description": "Search products (dishes) by text query",
                          "parameters": {
                            "type": "object",
                            "properties": { "query": { "type": "string" } },
                            "required": ["query"]
                          }
                        }
                        """),
                functionFromJson("""
                        {
                          "name": "getProductById",
                          "description": "Get a product by id",
                          "parameters": {
                            "type": "object",
                            "properties": { "productId": { "type": "integer" } },
                            "required": ["productId"]
                          }
                        }
                        """),
                functionFromJson("""
                        {
                          "name": "getProductsByStore",
                          "description": "List products by store id",
                          "parameters": {
                            "type": "object",
                            "properties": { "storeId": { "type": "integer" } },
                            "required": ["storeId"]
                          }
                        }
                        """),
                functionFromJson("""
                        {
                          "name": "getProductsByCategory",
                          "description": "List products by category id",
                          "parameters": {
                            "type": "object",
                            "properties": { "categoryId": { "type": "integer" } },
                            "required": ["categoryId"]
                          }
                        }
                        """),
                functionFromJson("""
                        {
                          "name": "getCustomerAddresses",
                          "description": "List addresses for a customer",
                          "parameters": {
                            "type": "object",
                            "properties": { "customerId": { "type": "string" } },
                            "required": ["customerId"]
                          }
                        }
                        """)
        );

        Tool tool = Tool.builder()
                .functionDeclarations(declarations)
                .build();
        this.tools = List.of(tool);
    }

    private FunctionDeclaration functionFromJson(String json) {
        return FunctionDeclaration.fromJson(json);
    }
}
