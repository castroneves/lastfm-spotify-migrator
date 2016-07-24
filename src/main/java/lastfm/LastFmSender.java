package lastfm;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import exception.LastFmException;
import lastfm.domain.Response;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.config.LastFmConfig;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

/**
 * Created by castroneves on 03/04/2016.
 */
@Singleton
public class LastFmSender {
    private static final Logger logger = LoggerFactory.getLogger(LastFmSender.class);
    private static final String baseUrl = "http://ws.audioscrobbler.com/2.0/";

    private final Client client;
    private final String apiKey;
    private final String apiSecret;

    @Inject
    public LastFmSender(LastFmConfig config) {
        apiKey = config.getApiKey();
        apiSecret = config.getSecret();
        client = JerseyClientBuilder.newClient();
    }

    public Response topTracksRequest(final String username, final int limit, final String period) {
        WebTarget webResource = getTopTracksWebResource(username, limit, period);
        Response response = webResource.request(MediaType.APPLICATION_JSON_TYPE).
                accept(MediaType.APPLICATION_JSON_TYPE).get(Response.class);
        if (response.getError() != null) {
            throw new LastFmException(response.getMessage());
        }
        return response;
    }

    public Response topArtistsRequest(final String username, final int limit, final String period) {
        WebTarget webResource = getTopArtistsWebResource(username, limit, period);
        Response response = webResource.request(MediaType.APPLICATION_JSON_TYPE).
                accept(MediaType.APPLICATION_JSON_TYPE).get(Response.class);
        if (response.getError() != null) {
            throw new LastFmException(response.getMessage());
        }
        return response;
    }

    public Response lovedTracksRequest(final String username, final int limit) {
        WebTarget webResource = getLovedTracksWebResource(username, limit);
        Response response = webResource.request(MediaType.APPLICATION_JSON_TYPE).
                accept(MediaType.APPLICATION_JSON_TYPE).get(Response.class);
        if (response.getError() != null) {
            throw new LastFmException(response.getMessage());
        }
        return response;
    }

    private WebTarget getTopTracksWebResource(final String username, final int limit, final String period){
        WebTarget resource = client.target(baseUrl);
        return resource
                .queryParam("method", "user.gettoptracks")
                .queryParam("api_key", apiKey)
                .queryParam("user", username)
                .queryParam("format", "json")
                .queryParam("period", period)
                .queryParam("limit", String.valueOf(limit));
    }

    private WebTarget getTopArtistsWebResource(final String username, final int limit, final String period){
        WebTarget resource = client.target(baseUrl);
        return resource
                .queryParam("method", "user.gettopartists")
                .queryParam("api_key", apiKey)
                .queryParam("user", username)
                .queryParam("format", "json")
                .queryParam("period", period)
                .queryParam("limit", String.valueOf(limit));
    }

    private WebTarget getLovedTracksWebResource(final String username, final int limit){
        WebTarget resource = client.target(baseUrl);
        return resource
                .queryParam("method", "user.getlovedtracks")
                .queryParam("api_key", apiKey)
                .queryParam("user", username)
                .queryParam("format", "json")
                .queryParam("limit", String.valueOf(limit));
    }
}
