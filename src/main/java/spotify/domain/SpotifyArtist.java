package spotify.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Adam on 08/09/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpotifyArtist {

    public SpotifyArtist() {}

    public SpotifyArtist(String name) {
        this.name = name;
    }

    private String name;
    private String id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return name;
    }
}
