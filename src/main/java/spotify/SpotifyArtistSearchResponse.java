package spotify;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by adam on 03/05/16.
 */
public class SpotifyArtistSearchResponse {

    @JsonProperty("artists")
    private SpotifyArtistSearch result;

    public SpotifyArtistSearch getResult() {
        return result;
    }

    public void setResult(SpotifyArtistSearch result) {
        this.result = result;
    }
}
