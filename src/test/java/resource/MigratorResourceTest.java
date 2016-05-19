package resource;

import lastfm.LastFmSender;
import lastfm.domain.Response;
import lastfm.domain.TopTracks;
import lastfm.domain.Track;
import org.mockito.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import spotify.SpotifyCreatePlaylistResponse;
import spotify.SpotifySender;
import spotify.domain.*;
import token.TokenManager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by castroneves on 30/04/2016.
 */
public class MigratorResourceTest {

    @Mock
    private TokenManager tokenManager;

    @Mock
    private LastFmSender lastFmSender;

    @Mock
    private SpotifySender spotifySender;

    @InjectMocks
    private MigratorResource migratorResource;


    @Captor
    private ArgumentCaptor<List<String>> tracksCaptor;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void correctlyAddsSpotifyPlaylist() {
        Response response =  new Response();
        TopTracks topTracks = new TopTracks();
        List<Track> tracks = new ArrayList<>();
        tracks.add(new Track("Wild Wild Life", "Talking Heads"));
        tracks.add(new Track("Kiss That Frog", "Peter Gabriel"));
        topTracks.setTracks(tracks);
        response.setToptracks(topTracks);
        int limit = 100;
        String period = "6month";
        String username = "username";
        when(lastFmSender.topTracksRequest(username, limit, period)).thenReturn(response);
        AccessToken accessToken = new AccessToken();
        String accessCode = "accessCode";
        when(tokenManager.getOrLookup(eq(accessCode),any(Supplier.class))).thenReturn(accessToken);

        SpotifyTrackSearch spotifySearch = new SpotifyTrackSearch();
        spotifySearch.setItems(asList(new SpotifyTrack("Wild Wild Life", "Talking Heads")));
        when(spotifySender.searchForTrack("Talking Heads", "Wild Wild Life",accessToken)).thenReturn(new SpotifyTrackSearchResponse(spotifySearch));

        SpotifyTrackSearch spotifySearch1 = new SpotifyTrackSearch();
        spotifySearch.setItems(asList(new SpotifyTrack("Kiss That Frog", "Peter Gabriel")));
        when(spotifySender.searchForTrack("Peter Gabriel", "Kiss That Frog",accessToken)).thenReturn(new SpotifyTrackSearchResponse(spotifySearch1));

        UserProfile userProfile = new UserProfile();
        userProfile.setId("id");
        when(spotifySender.getUserId(accessToken.getAccessToken())).thenReturn(userProfile);

        SpotifyCreatePlaylistResponse playlistResponse = new SpotifyCreatePlaylistResponse();
        when(spotifySender.createPlaylist(anyString(),eq(true), eq(userProfile.getId()), eq(accessToken))).thenReturn(playlistResponse);

        migratorResource.createSpotifyPlaylistFromLastFmTopTracks(limit,period,username,accessCode,"some url");

        verify(spotifySender).addTracksToPlaylist(anyString(),eq("id"), tracksCaptor.capture(),eq(accessToken));
    }
}