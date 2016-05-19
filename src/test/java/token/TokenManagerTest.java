package token;

import com.fasterxml.jackson.databind.ObjectMapper;
import lastfm.domain.Response;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import redis.clients.jedis.Jedis;
import spotify.domain.AccessToken;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

/**
 * Created by adam on 20/04/16.
 */
public class TokenManagerTest {

    @Mock
    private JedisFactory jedisFactory;

    @Spy
    private ObjectMapper objectMapper;

    @InjectMocks
    private TokenManager tokenManager;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void returnsCachedValue() {
        Jedis jedis = mock(Jedis.class);
        when(jedisFactory.newJedis()).thenReturn(jedis);
        when(jedis.get("somekey")).thenReturn("{\"accessToken\":\"abcd\",\"tokenType\":\"A\",\"expiresIn\":null,\"refreshToken\":\"sdghdh\"}");

        AccessToken result = tokenManager.getOrLookup("somekey", () -> new AccessToken() );

        assertEquals(result.getAccessToken(), "abcd");
        assertEquals(result.getTokenType(), "A");
        assertEquals(result.getRefreshToken(), "sdghdh");
    }

    @Test
    public void fallsbackWhenKeyNotFound() {
        Jedis jedis = mock(Jedis.class);
        when(jedisFactory.newJedis()).thenReturn(jedis);
        when(jedis.get("somekey")).thenReturn("");
        final AccessToken expected = new AccessToken();

        AccessToken result = tokenManager.getOrLookup("somekey", () -> expected);

        assertSame(result,expected);
    }

    @Test
    public void fallsbackOnException() {
        when(jedisFactory.newJedis()).thenThrow(new RuntimeException());
        final AccessToken expected = new AccessToken();

        AccessToken result = tokenManager.getOrLookup("somekey", () -> expected);

        assertSame(result, expected);
    }


}