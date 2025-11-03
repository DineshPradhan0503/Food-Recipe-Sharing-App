package com.example.interactionservice.dto;

import com.example.interactionservice.model.InteractionType;

import javax.validation.constraints.NotNull;

public class InteractionDto {
    @NotNull(message = "Recipe ID is required")
    private Long recipeId;
    @NotNull(message = "Username is required")
    private String username;
    @NotNull(message = "Interaction type is required")
    private InteractionType type;
    private String content;

    public Long getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(Long recipeId) {
        this.recipeId = recipeId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public InteractionType getType() {
        return type;
    }

    public void setType(InteractionType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
