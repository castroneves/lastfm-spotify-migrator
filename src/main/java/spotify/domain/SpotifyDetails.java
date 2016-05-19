package spotify.domain;

/**
 * Created by Adam on 13/10/2015.
 */
public class SpotifyDetails {

    private String accessCode;
    private String playlistId;
    private String userId;



    public SpotifyDetails(String accessCode) {
        this.accessCode = accessCode;
    }


    public SpotifyDetails(String accessCode, String userId) {
        this.accessCode = accessCode;
        this.userId = userId;
    }

    public SpotifyDetails(String accessCode, String playlistId, String userId) {
        this.accessCode = accessCode;
        this.playlistId = playlistId;
        this.userId = userId;
    }

    public String getAccessCode() {
        return accessCode;
    }

    public String getPlaylistId() {
        return playlistId;
    }

    public String getUserId() {
        return userId;
    }
}
