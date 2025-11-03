package com.example.interactionservice.repository;

import com.example.interactionservice.model.Interaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InteractionRepository extends JpaRepository<Interaction, Long> {
    List<Interaction> findByRecipeId(Long recipeId);
}
