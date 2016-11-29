package spotify.domain;

import lastfm.domain.Track;

import java.util.Optional;

/**
 * Created by castroneves on 28/11/2016.
 */
public class BlockingResponse {
    private Optional<SpotifyTrackSearchResponse> response;
    private Track track;

    public BlockingResponse(Optional<SpotifyTrackSearchResponse> response, Track track) {
        this.response = response;
        this.track = track;
    }

    public Optional<SpotifyTrackSearchResponse> getResponse() {
        return response;
    }

    public Track getTrack() {
        return track;
    }
}
