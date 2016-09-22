package lastfm.domain;




import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by Adam on 27/04/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TopArtists {
    private List<Artist> artist;

    public List<Artist> getArtist() {
        return artist;
    }

    public void setArtist(List<Artist> artist) {
        this.artist = artist;
    }
}
