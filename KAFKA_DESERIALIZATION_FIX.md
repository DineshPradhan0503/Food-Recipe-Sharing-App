# Kafka Deserialization `ClassNotFoundException`: Final Fix

This document provides the definitive, verified fix for the `ClassNotFoundException` error that occurs during Kafka message consumption.

## 1. Why the `ClassNotFoundException` Happens

The error occurs because the producer service (`interactionservice`) serializes a full Java object, including its package name (`com.example.interactionservice.model.Interaction`), into the Kafka message. The consumer service (`trendingservice`) does not have this class in its codebase, and when it tries to deserialize the message, it fails.

## 2. The Solution: Decoupling with DTOs and Correct Configuration

The correct solution is to decouple the services by sending a generic JSON payload from the producer and configuring the consumer to deserialize it into a local Data Transfer Object (DTO).

### a. Producer Configuration (`interactionservice/application.properties`)

This configuration tells the `JsonSerializer` **not** to add the problematic `__TypeId__` header, ensuring a clean JSON payload.

```properties
spring.kafka.producer.properties.spring.json.add.type.headers=false
```

### b. Consumer DTO (`trendingservice/dto/InteractionDto.java`)

This is the local class that the consumer will use to deserialize the message.

```java
package com.example.trendingservice.dto;

public class InteractionDto {
    private Long recipeId;
    private String type;

    // Getters and setters
}
```

### c. Consumer Configuration (`trendingservice/kafka/KafkaConsumerConfig.java`)

This class provides the robust, type-safe configuration for the consumer. It creates separate factories for each message type and uses the `ErrorHandlingDeserializer`.

```java
package com.example.trendingservice.kafka;

import com.example.trendingservice.dto.InteractionDto;
import com.example.trendingservice.dto.RecipeDto;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
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

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

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
}
```

### d. Kafka Listener (`trendingservice/kafka/KafkaConsumer.java`)

This class contains the listener methods, which are now correctly configured to use the new container factories and DTOs.

```java
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
```
