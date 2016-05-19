package spotify.domain;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by castroneves on 16/04/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpotifyTrackSearch {
    private List<SpotifyTrack> items;


    public List<SpotifyTrack> getItems() {
        return items;
    }

    public void setItems(List<SpotifyTrack> items) {
        this.items = items;
    }
}
