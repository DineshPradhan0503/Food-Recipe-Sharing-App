package com.example.interactionservice.service;

import com.example.interactionservice.dto.InteractionDto;
import com.example.interactionservice.model.Interaction;
import com.example.interactionservice.repository.InteractionRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InteractionService {

    private final InteractionRepository interactionRepository;
    private final KafkaTemplate<String, Interaction> kafkaTemplate;

    public InteractionService(InteractionRepository interactionRepository, KafkaTemplate<String, Interaction> kafkaTemplate) {
        this.interactionRepository = interactionRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public List<Interaction> getInteractionsForRecipe(Long recipeId) {
        return interactionRepository.findByRecipeId(recipeId);
    }

    public Interaction createInteraction(InteractionDto interactionDto) {
        Interaction interaction = new Interaction();
        interaction.setRecipeId(interactionDto.getRecipeId());
        interaction.setUsername(interactionDto.getUsername());
        interaction.setType(interactionDto.getType());
        interaction.setContent(interactionDto.getContent());
        Interaction savedInteraction = interactionRepository.save(interaction);
        kafkaTemplate.send("interactions", savedInteraction);
        return savedInteraction;
    }
}
