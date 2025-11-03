package com.example.trendingservice;

import com.example.trendingservice.config.TestRedisConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
    "spring.redis.host=localhost",
    "spring.redis.port=6379"
})
@Import(TestRedisConfiguration.class)
@ActiveProfiles("test")
class TrendingServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
