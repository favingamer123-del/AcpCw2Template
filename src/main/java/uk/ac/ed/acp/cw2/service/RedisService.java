package uk.ac.ed.acp.cw2.service;

import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import uk.ac.ed.acp.cw2.data.RuntimeEnvironment;

import java.util.Set;

@Service
public class RedisService {

    private final RuntimeEnvironment environment;

    public RedisService(RuntimeEnvironment environment) {
        this.environment = environment;
    }

    private JedisPool newPool() {
        return new JedisPool(environment.getRedisHost(), environment.getRedisPort());
    }

    public String getString(String key) {
        try (JedisPool pool = newPool(); Jedis jedis = pool.getResource()) {
            return jedis.get(key);
        }
    }

    public void setString(String key, String value) {
        try (JedisPool pool = newPool(); Jedis jedis = pool.getResource()) {
            jedis.set(key, value);
        }
    }

    public boolean exists(String key) {
        try (JedisPool pool = newPool(); Jedis jedis = pool.getResource()) {
            return jedis.exists(key);
        }
    }

    public void putHashEntry(String hashName, String field, String value) {
        try (JedisPool pool = newPool(); Jedis jedis = pool.getResource()) {
            jedis.hset(hashName, field, value);
        }
    }

    public String getHashEntry(String hashName, String field) {
        try (JedisPool pool = newPool(); Jedis jedis = pool.getResource()) {
            return jedis.hget(hashName, field);
        }
    }

    public Set<String> keys(String pattern) {
        try (JedisPool pool = newPool(); Jedis jedis = pool.getResource()) {
            return jedis.keys(pattern);
        }
    }

    public void deleteKey(String key) {
        try (JedisPool pool = newPool(); Jedis jedis = pool.getResource()) {
            jedis.del(key);
        }
    }

    public void deleteAllKeys() {
        try (JedisPool pool = newPool(); Jedis jedis = pool.getResource()) {
            Set<String> keys = jedis.keys("*");
            if (!keys.isEmpty()) {
                jedis.del(keys.toArray(new String[0]));
            }
        }
    }
}