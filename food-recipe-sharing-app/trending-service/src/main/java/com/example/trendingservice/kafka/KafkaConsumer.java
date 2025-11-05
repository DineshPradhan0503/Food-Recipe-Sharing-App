package com.example.trendingservice.kafka;

import com.example.trendingservice.dto.InteractionDto;
import com.example.trendingservice.dto.RecipeDto;
import com.example.trendingservice.service.TrendingService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    private final TrendingService trendingService;

    public KafkaConsumer(TrendingService trendingService) {
        this.trendingService = trendingService;
    }

    @KafkaListener(topics = "new-recipes", containerFactory = "recipeListenerContainerFactory")
    public void listenNewRecipes(RecipeDto recipe) {
        trendingService.updateTrendingScore(recipe.getId(), 1);
    }

    @KafkaListener(topics = "interactions", containerFactory = "interactionListenerContainerFactory")
    public void listenInteractions(InteractionDto interaction) {
        trendingService.updateTrendingScore(interaction.getRecipeId(), 0.5);
    }
}
