# Kafka Configuration Error: Analysis and Fix

## 1. The Problem: Why `.getConfiguration()` Fails

The error `The method getConfiguration() is undefined for the type ConsumerFactory` occurs because you are trying to call a method that belongs to a specific implementation (`DefaultKafkaConsumerFactory`) on an interface (`ConsumerFactory`).

-   The `consumerFactory()` method in your original code returned a `ConsumerFactory<String, Object>`.
-   While the actual object returned was a `DefaultKafkaConsumerFactory`, the reference is of the interface type, which does not have the `.getConfiguration()` method.
-   This approach is also not type-safe, as it requires casting and manual configuration, which can lead to runtime errors.

## 2. The Solution: Type-Safe, Multi-Factory Configuration

The correct and recommended approach is to create separate, strongly-typed consumer factories for each message type (DTO). This is more robust, easier to maintain, and eliminates the error.

### a. The Corrected `KafkaConsumerConfig.java`

This new configuration uses a private helper method (`createConsumerFactory`) to build a strongly-typed `ConsumerFactory` for any given DTO class. It then creates a separate `ConcurrentKafkaListenerContainerFactory` for each message type.

```java
package com.example.trendingservice.kafka;

import com.example.trendingservice.dto.InteractionDto;
import com.example.trendingservice.dto.RecipeDto;
import com.example.trendingservice.service.TrendingService;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    public KafkaConsumerConfig(TrendingService trendingService) {
        this.trendingService = trendingService;
    }

    private <T> ConsumerFactory<String, T> createConsumerFactory(Class<T> dtoClass) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        JsonDeserializer<T> jsonDeserializer = new JsonDeserializer<>(dtoClass);
        jsonDeserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new ErrorHandlingDeserializer<>(jsonDeserializer));
    }

    @Bean
    public ConsumerFactory<String, RecipeDto> recipeConsumerFactory() {
        return createConsumerFactory(RecipeDto.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, RecipeDto> recipeListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, RecipeDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(recipeConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, InteractionDto> interactionConsumerFactory() {
        return createConsumerFactory(InteractionDto.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, InteractionDto> interactionListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, InteractionDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(interactionConsumerFactory());
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
```

### b. The Corrected `application.properties`

The deserializer properties have been removed from the properties file, as they are now handled in the `KafkaConsumerConfig`.

```properties
server.port=8085

spring.application.name=trending-service

eureka.client.service-url.defaultZone=http://localhost:8761/eureka/

spring.redis.host=localhost
spring.redis.port=6379

spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=trending-group
```
