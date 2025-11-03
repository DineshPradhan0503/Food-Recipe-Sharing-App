package com.example.interactionservice.controller;

import com.example.interactionservice.dto.InteractionDto;
import com.example.interactionservice.model.Interaction;
import com.example.interactionservice.service.InteractionService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/interactions")
public class InteractionController {

    private final InteractionService interactionService;

    public InteractionController(InteractionService interactionService) {
        this.interactionService = interactionService;
    }

    @GetMapping("/recipe/{recipeId}")
    public List<Interaction> getInteractionsForRecipe(@PathVariable Long recipeId) {
        return interactionService.getInteractionsForRecipe(recipeId);
    }

    @PostMapping
    public Interaction createInteraction(@Valid @RequestBody InteractionDto interactionDto) {
        return interactionService.createInteraction(interactionDto);
    }
}
