package domain.response;

/**
 * Created by adam on 19/11/16.
 */
public class CreatePlaylistResponse {
    private String playlistName;
    private String spotifyId;

    public CreatePlaylistResponse(String playlistName, String spotifyId) {
        this.playlistName = playlistName;
        this.spotifyId = spotifyId;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public String getSpotifyId() {
        return spotifyId;
    }
}
