package spotify.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adam on 28/02/2016.
 */
public class EmptySpotifyResponse implements SpotifyResponse {
    @Override
    public Integer getTotal() {
        return 0;
    }

    @Override
    public List<?> getItems() {
        return new ArrayList<>();
    }
}
