package az.jrs.sweet.util;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;


@Component
@RequiredArgsConstructor
public class CacheUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    public void writeToCache(String key, Object value, Long ttl) {
        redisTemplate.opsForValue().set(key, value, ttl, TimeUnit.MINUTES);
    }

    public boolean readFromCache(String key, Object expectedValue) {
        Object cachedValue = redisTemplate.opsForValue().get(key);
        return cachedValue != null && cachedValue.equals(expectedValue);
    }
    public Object onlyReadWithKeyFromCache(String key) {
        return redisTemplate.opsForValue().get(key);
    }


    public void deleteFromCache(String key) {
        redisTemplate.delete(key);
    }

    public void incrementCacheValue(String key) {
        redisTemplate.opsForValue().increment(key);
    }

    public Integer getAttemptsFromCache(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? (Integer) value : 0;
    }

}
