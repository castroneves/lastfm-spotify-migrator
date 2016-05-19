package lastfm.domain;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by Adam on 28/01/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SimilarArtists {
    private List<Artist> artist;

    public List<Artist> getArtist() {
        return artist;
    }

    public void setArtist(List<Artist> artist) {
        this.artist = artist;
    }
}
