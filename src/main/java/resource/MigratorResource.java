package resource;

import com.google.inject.Inject;
import domain.response.CreatePlaylistResponse;
import lastfm.LastFmSender;
import lastfm.domain.Artist;
import lastfm.domain.Response;
import lastfm.domain.Track;
import spotify.SpotifyArtistSearchResponse;
import spotify.SpotifyCreatePlaylistResponse;
import spotify.SpotifySender;
import spotify.domain.AccessToken;
import spotify.domain.SpotifyTrackSearchResponse;
import spotify.domain.UserProfile;
import token.TokenManager;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Created by castroneves on 03/04/2016.
 */
@Path("/migrator")
@Produces({"application/json"})
public class MigratorResource {
    @Inject
    private LastFmSender lastFmSender;

    @Inject
    private SpotifySender spotifySender;

    @Inject
    private TokenManager tokenManager;

    @Path("/toptracks/{limit}/{period}/{username}/{accessToken}/{redirectUrl}")
    @GET
    public CreatePlaylistResponse createSpotifyPlaylistFromLastFmTopTracks(@PathParam("limit") int limit,
                                                                           @PathParam("period") String period,
                                                                           @PathParam("username") String username,
                                                                           @PathParam("accessToken") String accessToken,
                                                                           @PathParam("redirectUrl") String redirectUrl) {
        Response response = lastFmSender.topTracksRequest(username, limit, period);
        List<Track> tracks = response.getToptracks().getTracks();
        AccessToken token = tokenManager.getOrLookup(accessToken, () -> spotifySender.getAuthToken(accessToken, redirectUrl));
        List<SpotifyTrackSearchResponse> spotifyTrackSearchResponses = tracks.stream()
                .map(t -> spotifySender.searchForTrack(t.getArtist(), t.getName(), token))
                .collect(toList());
        List<String> trackIds = spotifyTrackSearchResponses.stream()
                .filter(s -> s.getTracks() != null)
                .filter(s -> s.getTracks().getItems() != null)
                .filter(s -> s.getTracks().getItems().size() > 0)
                .map(s -> s.getTracks().getItems().get(0).getId())
                .collect(toList());
        UserProfile userProfile = spotifySender.getUserId(token.getAccessToken());
        String playlistName = "LastFM " + username + "Top " + limit + " " + period;
        SpotifyCreatePlaylistResponse playlist = spotifySender.createPlaylist(playlistName, true, userProfile.getId(), token);
        spotifySender.addTracksToPlaylist(playlist.getId(), userProfile.getId(), trackIds, token);
        return new CreatePlaylistResponse(playlistName, playlist.getId());
    }

    @Path("/followartists/{limit}/{period}/{username}/{accessToken}/{redirectUrl}")
    @GET
    public void followTopLastFmArtistsInSpotify(@PathParam("limit") int limit,
                                                @PathParam("period") String period,
                                                @PathParam("username") String username,
                                                @PathParam("accessToken") String accessToken,
                                                @PathParam("redirectUrl") String redirectUrl) {

        Response response = lastFmSender.topArtistsRequest(username, limit, period);
        List<Artist> artists = response.getTopartists().getArtist();
        AccessToken token = tokenManager.getOrLookup(accessToken, () -> spotifySender.getAuthToken(accessToken, redirectUrl));
        List<SpotifyArtistSearchResponse> spotifyArtistSearchResponses = artists.stream()
                        .map(a -> spotifySender.searchForArtist(a.getName(), token))
                        .collect(toList());

        List<String> artistIds = spotifyArtistSearchResponses.stream()
                .filter(s -> s.getResult() != null)
                .filter(s -> s.getResult().getArtists() != null)
                .filter(s -> s.getResult().getArtists().size() > 0)
                .map(s -> s.getResult().getArtists().get(0).getId())
                .collect(toList());

        spotifySender.followArtists(artistIds, token);
    }
}
