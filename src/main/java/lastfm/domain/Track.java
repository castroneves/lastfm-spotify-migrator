package lastfm.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by castroneves on 03/04/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Track {
    private String name;
    private String artist;


    public Track() {
    }

    public Track(String name, String artist) {
        this.name = name;
        this.artist = artist;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    @JsonProperty("artist")
    public void setArtist(Artist artist) {
        this.artist = artist.getName();
    }

    @Override
    public String toString() {
        return "Track{" +
                "name='" + name + '\'' +
                ", artist='" + artist + '\'' +
                '}';
    }
}
