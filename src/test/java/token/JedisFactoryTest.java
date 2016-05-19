package token;

import org.testng.annotations.Test;
import redis.clients.jedis.Jedis;
import service.config.JedisConfig;

import static org.testng.Assert.assertEquals;

public class JedisFactoryTest {
    private final String host = "host";
    private final int port = 2222;
    private final String password = "password";

    @Test
    public void jedisCorrectlyInstantiated() {
        JedisConfig config = new JedisConfig();
        config.setHost(host);
        config.setPassword(password);
        config.setPort(port);
        JedisFactory factory = new JedisFactory(config);

        Jedis jedis = factory.newJedis();
        assertEquals(jedis.getClient().getHost(), host);
        assertEquals(jedis.getClient().getPort(), port);

    }

}