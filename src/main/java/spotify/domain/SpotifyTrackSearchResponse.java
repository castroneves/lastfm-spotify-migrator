package spotify.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by castroneves on 10/04/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpotifyTrackSearchResponse {
    private SpotifyTrackSearch tracks;
    private Integer rank;

    public SpotifyTrackSearchResponse() {
    }

    public SpotifyTrackSearchResponse(SpotifyTrackSearchResponse that, Integer rank) {
        this.rank = rank;
        this.tracks = that.getTracks();
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

    public Integer getRank() {
        return rank;
    }

    @Override
    public String toString() {
        return tracks.getItems().toString();
    }
}
