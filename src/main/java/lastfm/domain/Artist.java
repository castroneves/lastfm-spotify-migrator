package lastfm.domain;



import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Adam on 27/04/2015.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Artist {
    private String name;
    private String playcount;
    private Integer rank;
    private String match;


    public Artist() {
    }

    public Artist(String name, String playcount, Integer rank) {
        this.name = name;
        this.playcount = playcount;
        this.rank = rank;
    }

    public Artist(String name, String playcount, Integer rank, String match) {
        this(name, playcount, rank);
        this.match = match;
    }


    public String getMatch() {
        return match;
    }

    public void setMatch(String match) {
        this.match = match;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlaycount() {
        return playcount;
    }
    @JsonIgnore
    public Integer getPlaycountInt() {
        return Integer.valueOf(playcount);
    }

    public void setPlaycount(String playcount) {
        this.playcount = playcount;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Artist artist = (Artist) o;

        if (!name.equals(artist.name)) return false;

        return true;
    }

    public void setReccoRank(Integer rank) {
        this.rank = rank;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "Artist{" +
                "name='" + name + '\'' +
                '}';
    }
}
