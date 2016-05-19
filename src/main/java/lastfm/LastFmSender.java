package lastfm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import exception.LastFmException;
import lastfm.domain.Response;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.config.LastFmConfig;

import javax.ws.rs.core.MediaType;
import java.io.IOException;

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
        ClientConfig cc = new DefaultClientConfig();
        cc.getClasses().add(JacksonJsonProvider.class);
        client = Client.create(cc);
    }

    public Response topTracksRequest(final String username, final int limit, final String period) {
        WebResource webResource = getTopTracksWebResource(username, limit, period);
        Response response = webResource.accept(MediaType.APPLICATION_JSON_TYPE).
                type(MediaType.APPLICATION_JSON_TYPE).get(Response.class);
        if (response.getError() != null) {
            throw new LastFmException(response.getMessage());
        }
        return response;
    }

    public Response topArtistsRequest(final String username, final int limit, final String period) {
        WebResource webResource = getTopArtistsWebResource(username, limit, period);
        Response response = webResource.accept(MediaType.APPLICATION_JSON_TYPE).
                type(MediaType.APPLICATION_JSON_TYPE).get(Response.class);
        if (response.getError() != null) {
            throw new LastFmException(response.getMessage());
        }
        return response;
    }

    public Response lovedTracksRequest(final String username, final int limit) {
        WebResource webResource = getLovedTracksWebResource(username, limit);
        Response response = webResource.accept(MediaType.APPLICATION_JSON_TYPE).
                type(MediaType.APPLICATION_JSON_TYPE).get(Response.class);
        if (response.getError() != null) {
            throw new LastFmException(response.getMessage());
        }
        return response;
    }

    private WebResource getTopTracksWebResource(final String username, final int limit, final String period){
        WebResource resource = client.resource(baseUrl);
        return resource
                .queryParam("method", "user.gettoptracks")
                .queryParam("api_key", apiKey)
                .queryParam("user", username)
                .queryParam("format", "json")
                .queryParam("period", period)
                .queryParam("limit", String.valueOf(limit));
    }

    private WebResource getTopArtistsWebResource(final String username, final int limit, final String period){
        WebResource resource = client.resource(baseUrl);
        return resource
                .queryParam("method", "user.gettopartists")
                .queryParam("api_key", apiKey)
                .queryParam("user", username)
                .queryParam("format", "json")
                .queryParam("period", period)
                .queryParam("limit", String.valueOf(limit));
    }

    private WebResource getLovedTracksWebResource(final String username, final int limit){
        WebResource resource = client.resource(baseUrl);
        return resource
                .queryParam("method", "user.getlovedtracks")
                .queryParam("api_key", apiKey)
                .queryParam("user", username)
                .queryParam("format", "json")
                .queryParam("limit", String.valueOf(limit));
    }


    public static void main(String[] args) {
        LastFmConfig config = new LastFmConfig();
        config.setApiKey("0ba3650498bb88d7328c97b461fc3636");
        LastFmSender sender = new LastFmSender(config);

        Response castroneves121 = sender.topArtistsRequest("castroneves121", 100, "12month");
        castroneves121.getTopartists().getArtist().stream().forEach(System.out::println);
    }
}
