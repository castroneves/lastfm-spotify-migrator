package lastfm.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by castroneves on 03/04/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Track {
    private String name;
    private String artist;
    private Integer rank;


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

    @JsonIgnore
    public Integer getRankValue() {
        return rank;
    }

    @JsonIgnore
    public void setRankValue(Integer rank) {
        this.rank = rank;
    }

    @JsonProperty("@attr")
    public Map<String,Integer> getRank() {
        Map<String,Integer> result = new HashMap<>();
        result.put("rank", this.rank);
        return result;
    }

    @JsonProperty("@attr")
    public void setRank(Map<String,Integer> attr) {
        this.rank = attr.get("rank");
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
