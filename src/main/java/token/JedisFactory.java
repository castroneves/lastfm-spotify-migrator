package token;

import com.google.inject.Inject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisShardInfo;
import service.config.JedisConfig;

/**
 * Created by adam.heinke on 15/09/2015.
 */
public class JedisFactory {

    private final String host;
    private final int port;
    private final String password;

    @Inject
    public JedisFactory(JedisConfig config) {
        this.host = config.getHost();
        this.port = config.getPort();
        this.password = config.getPassword();
    }

    public Jedis newJedis() {
        JedisShardInfo shardInfo = new JedisShardInfo(host, port);
        shardInfo.setPassword(password);
        return new Jedis(shardInfo);
    }
}
