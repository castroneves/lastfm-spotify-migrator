package spotify.domain;

import lastfm.domain.Track;

import java.util.Optional;

/**
 * Created by castroneves on 28/11/2016.
 */
public class BlockingResponse {
    private Optional<SpotifyTrackSearchResponse> response;
    private Track track;
    private int status;

    public BlockingResponse(Optional<SpotifyTrackSearchResponse> response, Track track, int status) {
        this.response = response;
        this.track = track;
        this.status = status;
    }

    public Optional<SpotifyTrackSearchResponse> getResponse() {
        return response;
    }

    public Track getTrack() {
        return track;
    }

    public int getStatus() {
        return status;
    }
}
