package exception;

/**
 * Created by Adam on 15/06/2015.
 */
public class LastFmException extends RuntimeException {

    public LastFmException(String message) {
        super("LastFM error - " + message);
    }
}
