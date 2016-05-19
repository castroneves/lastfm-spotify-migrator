package spotify;

import java.util.List;

/**
 * Created by adam on 03/05/16.
 */
public class SpotifyFollowArtistsRequest {

    private List<String> ids;

    public SpotifyFollowArtistsRequest(List<String> ids) {
        this.ids = ids;
    }

    public List<String> getIds() {
        return ids;
    }
}
