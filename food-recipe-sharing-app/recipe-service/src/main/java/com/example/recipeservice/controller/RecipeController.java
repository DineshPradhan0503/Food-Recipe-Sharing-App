package com.example.recipeservice.controller;

import com.example.recipeservice.dto.RecipeDto;
import com.example.recipeservice.model.Recipe;
import com.example.recipeservice.service.RecipeService;
import com.example.recipeservice.util.SecurityUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping
    public List<Recipe> getAllRecipes(@RequestHeader("Authorization") String token) {
        SecurityUtil.ensureUser(token);
        return recipeService.getAllRecipes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getRecipeById(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        SecurityUtil.ensureUser(token);
        Recipe recipe = recipeService.getRecipeById(id);
        if (recipe == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(recipe);
    }

    @PostMapping
    public Recipe createRecipe(@Valid @RequestBody RecipeDto recipeDto, @RequestHeader("Authorization") String token) {
        SecurityUtil.ensureUser(token);
        return recipeService.createRecipe(recipeDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Recipe> updateRecipe(@PathVariable Long id, @Valid @RequestBody RecipeDto recipeDto, @RequestHeader("Authorization") String token) {
        SecurityUtil.ensureUser(token);
        Recipe updatedRecipe = recipeService.updateRecipe(id, recipeDto);
        if (updatedRecipe == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedRecipe);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        SecurityUtil.ensureAdmin(token);
        recipeService.deleteRecipe(id);
        return ResponseEntity.noContent().build();
    }
}
