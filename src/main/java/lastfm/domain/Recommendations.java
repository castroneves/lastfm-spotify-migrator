package lastfm.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Recommendations {
    public Recommendations() {
    }

    public Recommendations(List<Artist> artist) {
        this.artist = artist;
    }

    private List<Artist> artist;

    public List<Artist> getArtist() {
        return artist;
    }

    public void setArtist(List<Artist> artist) {
        this.artist = artist;
    }
}
