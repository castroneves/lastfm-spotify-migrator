package exception;

import org.eclipse.jetty.http.HttpStatus;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * Created by adam.heinke on 21/10/2015.
 */
public class LastFmExceptionMapper implements ExceptionMapper<LastFmException> {

    @Override
    public Response toResponse(LastFmException e) {
        return Response.status(HttpStatus.SERVICE_UNAVAILABLE_503).entity(e).type(MediaType.APPLICATION_JSON_TYPE).build();
    }
}
