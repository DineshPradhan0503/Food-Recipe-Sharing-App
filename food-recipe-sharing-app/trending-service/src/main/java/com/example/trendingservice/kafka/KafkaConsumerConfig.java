package com.example.trendingservice.kafka;

import com.example.trendingservice.dto.InteractionDto;
import com.example.trendingservice.dto.RecipeDto;
import com.example.trendingservice.service.TrendingService;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    private final TrendingService trendingService;

    public KafkaConsumerConfig(TrendingService trendingService) {
        this.trendingService = trendingService;
    }

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "trending-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "com.example.trendingservice.dto.InteractionDto");

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, InteractionDto> interactionListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, InteractionDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(consumerFactory().getConfiguration(), new StringDeserializer(), new JsonDeserializer<>(InteractionDto.class)));
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, RecipeDto> recipeListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, RecipeDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(consumerFactory().getConfiguration(), new StringDeserializer(), new JsonDeserializer<>(RecipeDto.class)));
        return factory;
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
