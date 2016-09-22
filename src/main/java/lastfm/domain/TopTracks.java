package lastfm.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by Adam on 27/04/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TopTracks {
    @JsonProperty("track")
    private List<Track> tracks;

    public List<Track> getTracks() {
        return tracks;
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }
}
