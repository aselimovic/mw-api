package ba.qss.m2m.mw.api;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import ba.qss.framework.dataaccess.DAOException;

@Provider
public class DAOExceptionMapper implements ExceptionMapper<DAOException> {

	/**
	 * RESTful Java with JAX-RS 2.0, Second Edition (O'Reilly Media) -
	 * Chapter 7: Server Responses and Exception Handling - Exception Mapping 
	 */
	
	// java - How should I log uncaught exceptions in my RESTful JAX-RS web service? - Stack Overflow
	// http://stackoverflow.com/questions/19621653/how-should-i-log-uncaught-exceptions-in-my-restful-jax-rs-web-service
	
	public Response toResponse(DAOException e) {
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
				.entity(e.getMessage())
				.type("text/plain")
				// IllegalArgumentException
				.build();
	}
}
