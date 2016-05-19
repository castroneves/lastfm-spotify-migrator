package spotify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sun.jersey.api.client.*;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.config.SpotifyConfig;
import spotify.domain.*;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.util.List;
import java.util.Optional;

/**
 * Created by castroneves on 03/04/2016.
 */
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
        ClientConfig cc = new DefaultClientConfig();
        cc.getClasses().add(JacksonJsonProvider.class);
        client = Client.create(cc);
    }

    public UserProfile getUserId(final String accessCode) {
        WebResource resource = client.resource("https://api.spotify.com/v1/me");
        return resource.header("Authorization", "Bearer " + accessCode).accept(MediaType.APPLICATION_JSON_TYPE)
                .get(UserProfile.class);
    }

    public AccessToken getAuthToken(final String authCode, final String redirectUrl) {
        WebResource resource = client.resource(baseUrl);
        MultivaluedMap<String, String> request = new MultivaluedMapImpl();
        request.add("grant_type", "authorization_code");
        request.add("code", authCode);
        request.add("redirect_uri", redirectUrl);
        request.add("client_id", clientId);
        request.add("client_secret", secret);
        return resource.accept(MediaType.APPLICATION_JSON_TYPE).
                type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(AccessToken.class, request);
    }

    public SpotifyTrackSearchResponse searchForTrack(String artist, String trackName, AccessToken token) {
        WebResource resource = client.resource("https://api.spotify.com/v1/search")
                .queryParam("limit", "1")
                .queryParam("type", "track")
                .queryParam("q", "artist:" + artist + " track:" + trackName);

        Optional<SpotifyTrackSearchResponse> response = searchWithRetries(resource, token, SpotifyTrackSearchResponse.class);
        return response.orElse(new SpotifyTrackSearchResponse());
    }

    public SpotifyArtistSearchResponse searchForArtist(String artist, AccessToken token) {
        WebResource resource = client.resource("https://api.spotify.com/v1/search")
                .queryParam("limit", "1")
                .queryParam("type", "artist")
                .queryParam("q", artist);
        Optional<SpotifyArtistSearchResponse> response = searchWithRetries(resource, token, SpotifyArtistSearchResponse.class);
        return response.orElse(new SpotifyArtistSearchResponse());
    }

    private <T> Optional<T> searchWithRetries(WebResource resource, AccessToken token, Class<T> clazz) {
        int retires = 0;
        while (retires < MAX_RETRIES) {
            try {
                return Optional.of(resource.header("Authorization", "Bearer " + token.getAccessToken()).accept(MediaType.APPLICATION_JSON_TYPE)
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
        System.out.println("Timeout, empty reponse being returned");
        return Optional.empty();
    }


    public void followArtists(List<String> artists, AccessToken token) {
        List<List<String>> partitions = Lists.partition(artists, 50);
        partitions.stream().forEach(a -> followArtistsInPartition(a, token));
    }

    private void followArtistsInPartition(List<String> artists, AccessToken token) {
        WebResource resource = client
                .resource("https://api.spotify.com/v1/me/following")
                .queryParam("type", "artist");
        SpotifyFollowArtistsRequest request = new SpotifyFollowArtistsRequest(artists);
        resource.header("Authorization", "Bearer " + token.getAccessToken())
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .put(request);
    }


    public SpotifyCreatePlaylistResponse createPlaylist(String name, boolean isPublic, String ownerId, AccessToken token) {
        WebResource resource = client.resource("https://api.spotify.com/v1/users/" + ownerId + "/playlists");
        return resource
                .header("Authorization", "Bearer " + token.getAccessToken())
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .post(SpotifyCreatePlaylistResponse.class, new SpotifyCreatePlaylistRequest(name, isPublic));
    }

    public void addTrackToPlaylist(String playlistId, String ownerId, String trackId, AccessToken token) {
        WebResource resource = client.resource("https://api.spotify.com/v1/users/" + ownerId + "/playlists/" + playlistId + "/tracks")
                .queryParam("uris", "spotify:track:" + trackId);
        resource.header("Authorization", "Bearer " + token.getAccessToken())
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .post(SpotifyCreatePlaylistResponse.class);
    }

    public void addTracksToPlaylist(String playlistId, String ownerId, List<String> tracks, AccessToken token) {
        List<List<String>> partitions = Lists.partition(tracks, 100);
        partitions.stream().forEach(t -> addTracksInPartitionToPlaylist(playlistId, ownerId, t, token));
    }

    private void addTracksInPartitionToPlaylist(String playlistId, String ownerId, List<String> tracks, AccessToken token) {
        System.out.println("Sending tracks request");
        SpotifyAddToPlaylistRequest request = new SpotifyAddToPlaylistRequest(tracks);
        WebResource resource = client.resource("https://api.spotify.com/v1/users/" + ownerId + "/playlists/" + playlistId + "/tracks");
        resource.header("Authorization", "Bearer " + token.getAccessToken())
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .post(SpotifyCreatePlaylistResponse.class, request);
    }


    public static void main(String[] args) {
        SpotifyConfig config = new SpotifyConfig();
        config.setClientId("4f9ea44544be4b789e54bfd9c23ebdc9");
        config.setSecret("9faf4223661841fab665172c1dd04550");

        SpotifySender sender = new SpotifySender(config);
        AccessToken token = new AccessToken();
        token.setAccessToken("BQB9lb-VMHQcxP1YcGjpUf8xGTvbthLzqXtUyNw9gQCBH8QxVSSO6c2KWnoXHW98nR421gmPImzX9XRahaQ4qIQMh0Gj8x1YZ_bBO1uuHsyNR48ytIPeTL4DnN42QopD1cxnbhUYpFjMqbWvMzGp9eDhLTCi4qPUfKtG8vdGvEXKCx5EwVKg_8ktwu0EG81Rh7BoWx_ED7uI897_nIL_JA_7Y4-9dFCUEIS6raNQJXL-VmUdmjbDJU7WzhSXSgarhinu5e-HvIxn20TR6pUh5XEAQ6XpfKw99xu8hVLr30fUyGjLt2U0");
        SpotifyTrackSearchResponse trackSearchResponse = sender.searchForTrack("Mental As Anything", "Live it up", token);
        UserProfile userId = sender.getUserId(token.getAccessToken());
        SpotifyCreatePlaylistResponse playlist = sender.createPlaylist("LastFM playlist2", true, userId.getId(), token);
        sender.addTrackToPlaylist(playlist.getId(), userId.getId(), trackSearchResponse.getTracks().getItems().get(0).getId(), token);
        System.out.println(playlist.getId());
    }
}
