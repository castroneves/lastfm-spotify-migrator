package spotify;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.config.SpotifyConfig;
import spotify.domain.AccessToken;
import spotify.domain.SpotifyCreatePlaylistRequest;
import spotify.domain.SpotifyTrackSearchResponse;
import spotify.domain.UserProfile;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.util.List;
import java.util.Optional;

@Singleton
public class SpotifySender {
    private static final Logger logger = LoggerFactory.getLogger(SpotifySender.class);

    private final Client client;

    private static final String baseUrl = "https://accounts.spotify.com/api/token";
    private static final String tracksUrl = "https://api.spotify.com/v1/me/tracks";
    private static final int MAX_RETRIES = 3;

    private final String clientId;
    private final String secret;

    @Inject
    public SpotifySender(SpotifyConfig config) {
        clientId = config.getClientId();
        secret = config.getSecret();
        client = JerseyClientBuilder.newClient();
    }

    public UserProfile getUserId(final String accessCode) {
        WebTarget resource = client.target("https://api.spotify.com/v1/me");
        return resource.request().header("Authorization", "Bearer " + accessCode).accept(MediaType.APPLICATION_JSON_TYPE)
                .get(UserProfile.class);
    }

    public AccessToken getAuthToken(final String authCode, final String redirectUrl) {
        WebTarget resource = client.target(baseUrl);
        MultivaluedMap<String, String> request = new MultivaluedHashMap<>();
        request.add("grant_type", "authorization_code");
        request.add("code", authCode);
        request.add("redirect_uri", redirectUrl);
        request.add("client_id", clientId);
        request.add("client_secret", secret);
        return resource.request(MediaType.APPLICATION_FORM_URLENCODED_TYPE).accept(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.form(request), AccessToken.class);
    }

    public SpotifyTrackSearchResponse searchForTrack(String artist, String trackName, AccessToken token) {
        WebTarget resource = client.target("https://api.spotify.com/v1/search")
                .queryParam("limit", "1")
                .queryParam("type", "track")
                .queryParam("q", "artist:" + artist + " track:" + trackName);

        Optional<SpotifyTrackSearchResponse> response = searchWithRetries(resource, token, SpotifyTrackSearchResponse.class);
        return response.orElse(new SpotifyTrackSearchResponse());
    }

    public SpotifyArtistSearchResponse searchForArtist(String artist, AccessToken token) {
        WebTarget resource = client.target("https://api.spotify.com/v1/search")
                .queryParam("limit", "1")
                .queryParam("type", "artist")
                .queryParam("q", artist);
        Optional<SpotifyArtistSearchResponse> response = searchWithRetries(resource, token, SpotifyArtistSearchResponse.class);
        return response.orElse(new SpotifyArtistSearchResponse());
    }

    private <T> Optional<T> searchWithRetries(WebTarget resource, AccessToken token, Class<T> clazz) {
        int retires = 0;
        while (retires < MAX_RETRIES) {
            try {
                return Optional.of(resource.request().header("Authorization", "Bearer " + token.getAccessToken()).accept(MediaType.APPLICATION_JSON_TYPE)
                        .get(clazz));
            } catch (Exception e) {
                try {
                    Thread.sleep(1500);
                    ++retires;
                } catch (InterruptedException e1) {
                }
                continue;
            }
        }
        logger.warn("Timeout, empty reponse being returned");
        return Optional.empty();
    }


    public void followArtists(List<String> artists, AccessToken token) {
        List<List<String>> partitions = Lists.partition(artists, 50);
        partitions.stream().forEach(a -> followArtistsInPartition(a, token));
    }

    private void followArtistsInPartition(List<String> artists, AccessToken token) {
        WebTarget resource = client
                .target("https://api.spotify.com/v1/me/following")
                .queryParam("type", "artist");
        SpotifyFollowArtistsRequest request = new SpotifyFollowArtistsRequest(artists);
        resource.request(MediaType.APPLICATION_JSON_TYPE).header("Authorization", "Bearer " + token.getAccessToken())
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.json(request));
    }


    public SpotifyCreatePlaylistResponse createPlaylist(String name, boolean isPublic, String ownerId, AccessToken token) {
        WebTarget resource = client.target("https://api.spotify.com/v1/users/" + ownerId + "/playlists");
        return resource.request(MediaType.APPLICATION_JSON_TYPE)
                .header("Authorization", "Bearer " + token.getAccessToken())
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(new SpotifyCreatePlaylistRequest(name, isPublic)), SpotifyCreatePlaylistResponse.class);
    }

    public void addTrackToPlaylist(String playlistId, String ownerId, String trackId, AccessToken token) {
        WebTarget resource = client.target("https://api.spotify.com/v1/users/" + ownerId + "/playlists/" + playlistId + "/tracks")
                .queryParam("uris", "spotify:track:" + trackId);
        resource.request(MediaType.APPLICATION_JSON_TYPE).header("Authorization", "Bearer " + token.getAccessToken())
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(SpotifyCreatePlaylistResponse.class));
    }

    public void addTracksToPlaylist(String playlistId, String ownerId, List<String> tracks, AccessToken token) {
        List<List<String>> partitions = Lists.partition(tracks, 100);
        partitions.stream().forEach(t -> addTracksInPartitionToPlaylist(playlistId, ownerId, t, token));
    }

    private void addTracksInPartitionToPlaylist(String playlistId, String ownerId, List<String> tracks, AccessToken token) {
        logger.debug("Sending tracks request");
        SpotifyAddToPlaylistRequest request = new SpotifyAddToPlaylistRequest(tracks);
        WebTarget resource = client.target("https://api.spotify.com/v1/users/" + ownerId + "/playlists/" + playlistId + "/tracks");
        resource.request(MediaType.APPLICATION_JSON_TYPE).header("Authorization", "Bearer " + token.getAccessToken())
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(request), SpotifyCreatePlaylistResponse.class);
    }
}
