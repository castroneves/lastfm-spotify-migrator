package spotify.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by castroneves on 10/04/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpotifyTrackSearchResponse{
    private SpotifyTrackSearch tracks;

    public SpotifyTrackSearchResponse() {
    }

    public SpotifyTrackSearchResponse(SpotifyTrackSearch tracks) {
        this.tracks = tracks;
    }

    public SpotifyTrackSearch getTracks() {
        return tracks;
    }

    public void setTracks(SpotifyTrackSearch tracks) {
        this.tracks = tracks;
    }
}
