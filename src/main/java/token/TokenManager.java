package token;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import spotify.domain.AccessToken;

import java.io.IOException;
import java.util.function.Supplier;

/**
 * Created by castroneves on 03/04/2016.
 */
public class TokenManager {

    private static final Logger logger = LoggerFactory.getLogger(TokenManager.class);

    @Inject
    private JedisFactory jedisFactory;

    @Inject
    private ObjectMapper mapper;

    public AccessToken getOrLookup(String redisKey, Supplier<AccessToken> func) {
        try (Jedis jedis = jedisFactory.newJedis()) {
            logger.info(redisKey + " " + jedis.ttl(redisKey));
            String json = jedis.get(redisKey);
            if (json != null && json != "") {
                return mapper.readValue(json, AccessToken.class);
            }
            return fallback(redisKey, func, jedis);
        } catch (Exception e) {
            return func.get();
        }
    }

    private AccessToken fallback(String key, Supplier<AccessToken> func, Jedis jedis) {
        AccessToken response = func.get();
        try {
            String inputJson = mapper.writeValueAsString(response);
            jedis.set(key, inputJson);
            jedis.expire(key, 3000);
        } catch (IOException e) {
            logger.error("Exception writing value to Redis");
        }
        return response;
    }

    public void setJedisFactory(JedisFactory jedisFactory) {
        this.jedisFactory = jedisFactory;
    }

    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }
}
