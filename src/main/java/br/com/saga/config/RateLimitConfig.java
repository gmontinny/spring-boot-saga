package br.com.saga.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class RateLimitConfig {

    @Value("${app.rate-limit.capacity}")
    private int capacity;

    @Value("${app.rate-limit.refill-tokens}")
    private int refillTokens;

    @Value("${app.rate-limit.refill-duration-seconds}")
    private int refillDurationSeconds;

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public Bucket resolveBucket(String key) {
        return buckets.computeIfAbsent(key, k -> createBucket());
    }

    private Bucket createBucket() {
        Bandwidth limit = Bandwidth.classic(capacity,
                Refill.greedy(refillTokens, Duration.ofSeconds(refillDurationSeconds)));
        return Bucket.builder().addLimit(limit).build();
    }
}
