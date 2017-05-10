package spotify;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lastfm.domain.Track;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.config.SpotifyConfig;
import spotify.domain.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Singleton
public class SpotifySender {
    private static final Logger logger = LoggerFactory.getLogger(SpotifySender.class);

    private final Client client;

    private static final String baseUrl = "https://accounts.spotify.com/api/token";
    private static final String tracksUrl = "https://api.spotify.com/v1/me/tracks";
    private static final int MAX_RETRIES = 1;
    public static final int TIMEOUT_SUBSEQUENT_MILLIS = 4000;

    private final String clientId;
    private final String secret;

    @Inject
    public SpotifySender(SpotifyConfig config) {
        clientId = config.getClientId();
        secret = config.getSecret();
        client = JerseyClientBuilder.newClient();
    }

    public UserProfile getUserId(final String accessCode) {
        logger.info("Fetching profile");
        WebTarget resource = client.target("https://api.spotify.com/v1/me");
        return withRetries(UserProfile.class, () -> resource.request().header("Authorization", "Bearer " + accessCode).accept(MediaType.APPLICATION_JSON_TYPE)
                .get()).orElseThrow(() -> new RuntimeException("Unable to fetch user profile"));
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

    private SearchResponseTuple searchForTrackAsync(Track track, AccessToken token) {
        WebTarget resource = client.target("https://api.spotify.com/v1/search")
                .queryParam("limit", "1")
                .queryParam("type", "track")
                .queryParam("q", "artist:" + track.getArtist() + " track:" + track.getName());

        Future<Response> future = resource.request().header("Authorization", "Bearer " + token.getAccessToken()).accept(MediaType.APPLICATION_JSON_TYPE).async()
                .get();
        return new SearchResponseTuple(future, track);
    }

    private static BlockingResponse blockForResult(SearchResponseTuple responseTuple) {
        int status = -1;
        try {
            Response result = responseTuple.getFuture().get(TIMEOUT_SUBSEQUENT_MILLIS, TimeUnit.MILLISECONDS);
            status = result.getStatus();
            Optional<SpotifyTrackSearchResponse> response = Optional.of(result.readEntity(SpotifyTrackSearchResponse.class));
//                logger.info("Returning track {} with errors {}", result.get(),errors);

            return new BlockingResponse(response, responseTuple.getTrack(),status);
        } catch (Exception e) {
            return new BlockingResponse(Optional.empty(), responseTuple.getTrack(), status);
        }
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

    private <T> Optional<T> withRetries(Class<T> clazz, Supplier<Response> func) {
        int exceptions = 0;
        do {
            Response response = func.get();
            if (response.getStatus() == 429) {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {}
                continue;
            }
            try {
                return Optional.of(response.readEntity(clazz));
            } catch (Exception e) {
                ++exceptions;
            }
        } while (exceptions < MAX_RETRIES);
        logger.warn("Timeout, empty reponse being returned");
        return Optional.empty();
    }

    public List<SpotifyTrackSearchResponse> searchForTracks(List<Track> inputTracks, AccessToken token) {
        List<Track> tracks = new ArrayList<>(inputTracks);
        List<SpotifyTrackSearchResponse> result = new ArrayList<>();
        List<Track> failures;
        Set<Integer> statusCodes = new HashSet<>();
        do {
            List<SearchResponseTuple> futures = tracks.stream()
                    .map(t -> searchForTrackAsync(t, token))
                    .collect(toList());

            List<BlockingResponse> initial = futures.stream()
                    .map(f -> blockForResult(f))
                    .collect(toList());

            result.addAll(initial.stream()
                    .filter(o -> o.getResponse().isPresent())
                    .map(r -> new SpotifyTrackSearchResponse(r.getResponse().get(), r.getTrack().getRankValue()))
                    .collect(toList()));

            statusCodes.addAll(initial.stream().map(r -> r.getStatus()).collect(Collectors.toList()));

            failures = initial.stream()
                    .filter(o -> !o.getResponse().isPresent())
                    .filter(r -> r.getStatus() == 429 || r.getStatus() == -1)
                    .map(BlockingResponse::getTrack)
                    .collect(toList());
            tracks = failures;
            logger.info("Results: {} Failures: {}", result.size(), failures.size());
            try {
                Thread.sleep( 1500);
            } catch (InterruptedException e) {}
        } while (failures.size() > 0);
        logger.info("statuscodes {}", statusCodes);
        return result.stream().sorted((x, y) -> x.getRank().compareTo(y.getRank())).collect(toList());
    }


    public SpotifyArtistSearchResponse searchForArtist(String artist, AccessToken token) {
        WebTarget resource = client.target("https://api.spotify.com/v1/search")
                .queryParam("limit", "1")
                .queryParam("type", "artist")
                .queryParam("q", artist);
        Optional<SpotifyArtistSearchResponse> response = searchWithRetries(resource, token, SpotifyArtistSearchResponse.class);
        return response.orElse(new SpotifyArtistSearchResponse());
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
        logger.info("Creating playlist");
        final WebTarget resource = client.target("https://api.spotify.com/v1/users/" + ownerId + "/playlists");
        final Entity<SpotifyCreatePlaylistRequest> json = Entity.json(new SpotifyCreatePlaylistRequest(name, isPublic));
        Optional<SpotifyCreatePlaylistResponse> result = withRetries(SpotifyCreatePlaylistResponse.class, () ->
                        resource.request(MediaType.APPLICATION_JSON_TYPE)
                                .header("Authorization", "Bearer " + token.getAccessToken())
                                .accept(MediaType.APPLICATION_JSON_TYPE)
                                .post(json)
        );
        return result.orElseThrow(() -> new RuntimeException("Playlist not created after retries"));
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
        logger.info("Adding tracks to partition");
        SpotifyAddToPlaylistRequest request = new SpotifyAddToPlaylistRequest(tracks);
        WebTarget resource = client.target("https://api.spotify.com/v1/users/" + ownerId + "/playlists/" + playlistId + "/tracks");
        withRetries(SpotifyCreatePlaylistResponse.class, () -> resource.request(MediaType.APPLICATION_JSON_TYPE).header("Authorization", "Bearer " + token.getAccessToken())
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(request))
        );
    }
}
