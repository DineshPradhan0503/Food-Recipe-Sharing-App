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
