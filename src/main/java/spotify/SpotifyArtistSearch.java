package spotify;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import spotify.domain.SpotifyArtist;

import java.util.List;

/**
 * Created by adam on 03/05/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpotifyArtistSearch {
    @JsonProperty("items")
    private List<SpotifyArtist> artists;

    public List<SpotifyArtist> getArtists() {
        return artists;
    }

    public void setArtists(List<SpotifyArtist> artists) {
        this.artists = artists;
    }
}
