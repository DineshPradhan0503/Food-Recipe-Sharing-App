package com.example.recipeservice.service;

import com.example.recipeservice.dto.RecipeDto;
import com.example.recipeservice.model.Recipe;
import com.example.recipeservice.repository.RecipeRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final KafkaTemplate<String, Recipe> kafkaTemplate;

    public RecipeService(RecipeRepository recipeRepository, KafkaTemplate<String, Recipe> kafkaTemplate) {
        this.recipeRepository = recipeRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    public Recipe getRecipeById(Long id) {
        return recipeRepository.findById(id).orElse(null);
    }

    public Recipe createRecipe(RecipeDto recipeDto) {
        Recipe recipe = new Recipe();
        recipe.setTitle(recipeDto.getTitle());
        recipe.setDescription(recipeDto.getDescription());
        recipe.setIngredients(recipeDto.getIngredients());
        recipe.setInstructions(recipeDto.getInstructions());
        recipe.setAuthor(recipeDto.getAuthor());
        Recipe savedRecipe = recipeRepository.save(recipe);
        kafkaTemplate.send("new-recipes", savedRecipe);
        return savedRecipe;
    }

    public Recipe updateRecipe(Long id, RecipeDto recipeDto) {
        Recipe recipe = recipeRepository.findById(id).orElse(null);
        if (recipe != null) {
            recipe.setTitle(recipeDto.getTitle());
            recipe.setDescription(recipeDto.getDescription());
            recipe.setIngredients(recipeDto.getIngredients());
            recipe.setInstructions(recipeDto.getInstructions());
            return recipeRepository.save(recipe);
        }
        return null;
    }

    public void deleteRecipe(Long id) {
        recipeRepository.deleteById(id);
    }
}
