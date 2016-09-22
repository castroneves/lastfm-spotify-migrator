package spotify.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Adam on 08/09/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpotifyTrack {
    private String id;
    private String name;
    private List<SpotifyArtist> artists;

    public SpotifyTrack() {
    }

    public SpotifyTrack(String name, String artistName) {
        this.name = name;
        artists = Arrays.asList(new SpotifyArtist(artistName));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SpotifyArtist> getArtists() {
        return artists;
    }

    public void setArtists(List<SpotifyArtist> artists) {
        this.artists = artists;
    }
}
