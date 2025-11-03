package com.example.trendingservice;

import com.example.trendingservice.config.TestRedisConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest(properties = {
    "spring.redis.host=localhost",
    "spring.redis.port=6379"
})
@Import(TestRedisConfiguration.class)
class TrendingServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
