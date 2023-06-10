package edu.neu.info5.DemoApplication.dao;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RedisDaoImpl implements RedisDao {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void postValue(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public boolean deleteValue(String key) {
        return redisTemplate.delete(key);
    }

    @Override
    public Object getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}
