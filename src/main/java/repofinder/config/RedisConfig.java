package repofinder.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import java.time.Duration;
import java.util.Collections;

@Configuration
public class RedisConfig {

    @Value("#{new Integer(environment['app.config.entry-ttl'])}")
    private int entryTtl;

    @Bean
    RedisCacheManager redisCacheManager(
        RedisConnectionFactory connectionFactory,
        RedisCacheConfiguration cacheConfig
    ) {
        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(cacheConfig)
            .withInitialCacheConfigurations(
                Collections.singletonMap("predefined", cacheConfig.disableCachingNullValues()))
            .transactionAware()
            .build();
    }

    @Bean
    RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofSeconds(entryTtl))
            .disableCachingNullValues();
    }

    @Bean
    public JedisConnectionFactory connectionFactory(RedisStandaloneConfiguration configuration) {
        return new JedisConnectionFactory(configuration);
    }

    @Bean
    @Profile("!container")
    public RedisStandaloneConfiguration redisStandaloneConfigurationLocal() {
        return new RedisStandaloneConfiguration("localhost", 6379);
    }

    @Bean
    @Profile("container")
    public RedisStandaloneConfiguration redisStandaloneConfigurationContainer() {
        return new RedisStandaloneConfiguration("cache", 6379);
    }
}
