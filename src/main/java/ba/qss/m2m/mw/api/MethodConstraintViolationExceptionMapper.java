package ba.qss.m2m.mw.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.hibernate.validator.method.MethodConstraintViolation;
import org.hibernate.validator.method.MethodConstraintViolationException;

@Provider
public class MethodConstraintViolationExceptionMapper
	implements ExceptionMapper<MethodConstraintViolationException> {

	public Response toResponse(MethodConstraintViolationException e) {
		Set<MethodConstraintViolation<?>> constraintViolations = null;
		// Maps know how to print themselves, showing the association with keys
		// and values. (TIJ4 p. 400)
		Map<String, String> entity = null;
		
		entity = new HashMap<String, String>();
		constraintViolations = e.getConstraintViolations();
		
		for (MethodConstraintViolation<?> mcv : constraintViolations) {
			entity.put(mcv.getParameterName(), mcv.getMessage());
		}
		
		// 6.5.1. 400 Bad Request
		// http://tools.ietf.org/html/rfc7231#section-6.5.1
		return Response.status(Response.Status.BAD_REQUEST)
				.entity(entity)
				.type("text/plain")
				.build();
	}
}
