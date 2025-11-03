package com.example.trendingservice.service;

import com.example.trendingservice.model.Recipe;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class TrendingService {

    private final RedisTemplate<String, Object> redisTemplate;

    public TrendingService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void updateTrendingScore(Long recipeId, double score) {
        redisTemplate.opsForZSet().incrementScore("trending_recipes", recipeId, score);
    }

    public Set<Object> getTrendingRecipes() {
        return redisTemplate.opsForZSet().reverseRange("trending_recipes", 0, 9);
    }
}
