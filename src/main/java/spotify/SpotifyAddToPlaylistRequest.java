package spotify;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by castroneves on 18/04/2016.
 */
public class SpotifyAddToPlaylistRequest {
    private List<String> uris;

    public SpotifyAddToPlaylistRequest(List<String> tracks) {
        uris = tracks.stream().map(t -> "spotify:track:" + t).collect(Collectors.toList());
    }

    public List<String> getUris() {
        return uris;
    }
}
