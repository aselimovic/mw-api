package ba.qss.m2m.mw.api;

import java.util.List;

import javax.persistence.OptimisticLockException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

//import org.jboss.resteasy.spi.validation.ValidateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import ba.qss.framework.IntValue;
import ba.qss.framework.dataaccess.DAOException;
import ba.qss.m2m.mw.dao.OracleMWDAOFactory;
import ba.qss.m2m.mw.dao.GeographicBoundaryDAO;
import ba.qss.m2m.mw.dao.GeographicBoundaryTO;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;

@Path("geographicboundary")
@Produces("application/json")
public class GeographicBoundaryResource {

	
	 private static final String CLASS_NAME =
	    		GeographicBoundaryResource.class.getName();
	    
	    // Chapter 1: Introduction
	    // http://logback.qos.ch/manual/introduction.html
	    public static final Logger logger = LoggerFactory.getLogger(
	    		// SLF4J Error Codes
	    		// http://www.slf4j.org/codes.html
	    		// TODO: Detected logger name mismatch 
	    		CLASS_NAME/*BindingEntityResource.class.getPackage().getName()*/);

	    // RESTEasy JAX-RS 2.3.7.Final - Chapter 21. JSON Support via Jackson p. 93
	    // http://docs.jboss.org/resteasy/docs/2.3.7.Final/userguide/pdf/resteasy-reference-guide-en-US.pdf
	    //GET /resources/stuff?callback=processStuffResponse

	    // java - JAX-RS --- How to return JSON and HTTP Status code together? - Stack Overflow
	    // http://stackoverflow.com/questions/4687271/jax-rs-how-to-return-json-and-http-status-code-together
	    @Context
	    private HttpServletResponse resp;
	    
	    @Context
	    public void setServletContext(ServletContext context) {
	    	String razvoj = null;
	    	
	    	// Returns null if the initialization parameter does not exist.
	    	razvoj = context.getInitParameter("Razvoj");
	    }
	    
		@GET
		// If the method invoked is not capable of producing one of the media types
		// requested in the HTTP request, the container must respond with a HTTP
		// "406 Not Acceptable" as specified by RFC 2616.
		//
		// RESTful Java with JAX-RS 2.0, Second Edition (O'Reilly Media) - Example
		// ex09_1: Conneg with JAX-RS
		// If the Accept header is application/xml, XML will be produced. If the
		// Accept header is JSON, the BindingEntityTO array will be outputted as
		// JSON.
		//
	    // RESTEasy JAX-RS 2.3.7.Final - 21.2. JSONP Support
	    //GET /resources/stuff?callback=processStuffResponse
		//Failed executing GET /bindingEntities: org.jboss.resteasy.core.NoMessageBodyWriterFoundFailure: Could not find MessageBodyWriter for response object of type: [Lba.qss.m2m.mw.dao.BindingEntityTO; of media type: application/xml
//	    @Produces({"application/xml","application/json"})
		// Integrating Bean Validation with JAX-RS in Java EE 6 » samaxes
		// http://www.samaxes.com/2013/01/beanvalidation-with-jaxrs-in-javaee6/
//		@ValidateRequest
		public GeographicBoundaryTO[] select(
				// Context (Java EE 6)
				// http://docs.oracle.com/javaee/6/api/javax/ws/rs/core/Context.html
				// See Also: Application, UriInfo, Request, HttpHeaders, SecurityContext, Providers
				 @QueryParam("filterExpression") String filterExpression 
				,@Size(max=50) @DefaultValue("GB.geo_id ASC") @QueryParam("sort") String sort // XXX: Sort key expression: ORDER BY sin(i)
				,@Min(value=0, message="{geographicboundary.sort.size}") @Max(100) @DefaultValue("0") @QueryParam("pageIndex") int pageIndex
				,@Min(0) @Max(1000) @DefaultValue("20") @QueryParam("pageSize") int pageSize) {
			GeographicBoundaryDAO geographicBoundaryDAO = null;
			List<GeographicBoundaryTO> geos = null;
	        GeographicBoundaryTO criteria = new GeographicBoundaryTO();
	        IntValue rowCount = new IntValue(0);
	        LoggerContext loggerContext = null;

	        //http://localhost:8280/mw-api/bindingEntities?pageIndex=0&pageSize=20&callback=processStuffResponse
	        
	        // Bean Validation
	        // http://beanvalidation.org/
	        
	        // 3.5. Exclude a Subsystem from a Deployment
	        // https://access.redhat.com/documentation/en-US/JBoss_Enterprise_Application_Platform/6.3/html/Development_Guide/Exclude_a_Subsystem_from_a_Deployment.html
			//<exclude-subsystems>
			//  <subsystem name="resteasy" />
			//</exclude-subsystems>
	        
	        // 3.4. Prevent a Module Being Implicitly Loaded
	        // https://access.redhat.com/documentation/en-US/JBoss_Enterprise_Application_Platform/6.3/html/Development_Guide/Prevent_a_Module_Being_Implicitly_Loaded.html
	        // For a web application (WAR) add this file to the WEB-INF directory.
	        // For an EJB archive (JAR) add it to the META-INF directory. 
			//<jboss-deployment-structure>
			//  <deployment>
			//    <exclusions>
			//      <module name="org.slf4j" />
			//      <module name="org.slf4j.impl" />
			//    </exclusions>
			//  </deployment>
			//</jboss-deployment-structure>
	        
	        // jboss7.x - Logback and Jboss 7 - don't work together? - Stack Overflow
	        // http://stackoverflow.com/questions/9518687/logback-and-jboss-7-dont-work-together
	        //Caused by: java.lang.ClassCastException: org.slf4j.impl.Slf4jLoggerFactory incompatible with ch.qos.logback.classic.LoggerContext
	        loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
	        // Logback's internal status information can be very useful in
	        // diagnosing logback-related problems. 
	        StatusPrinter.print(loggerContext);
	        
	        // SLF4J Error Codes
	        // http://www.slf4j.org/codes.html#multiple_bindings
			//18:39:28,969 ERROR [stderr] (http-/0.0.0.0:8280-4) SLF4J: Class path contains multiple SLF4J bindings.
			//18:39:28,969 ERROR [stderr] (http-/0.0.0.0:8280-4) SLF4J: Found binding in [vfs:/C:/dev/m2m/.metadata/.plugins/org.jboss.ide.eclipse.as.core/JBoss_EAP_6.1+_Runtime_Server1419254502752/deploy/mw-api.war/WEB-INF/lib/logback-classic-1.1.2.jar/org/slf4j/impl/StaticLoggerBinder.class]
			//18:39:28,969 ERROR [stderr] (http-/0.0.0.0:8280-4) SLF4J: Found binding in [vfs:/C:/dev/m2m/.metadata/.plugins/org.jboss.ide.eclipse.as.core/JBoss_EAP_6.1+_Runtime_Server1419254502752/deploy/mw-api.war/WEB-INF/lib/slf4j-log4j12-1.7.9.jar/org/slf4j/impl/StaticLoggerBinder.class]
	        
	        // log4j.properties Translator
	        // http://logback.qos.ch/translator/

	        // Chapter 8: Mapped Diagnostic Context
	        // http://logback.qos.ch/manual/mdc.html
			MDC.put("MSISDN", "38761712805");
	        
	        logger.info("Testna msg");
	        
			try {
	            // ucitaj BINDING_ENTITY tabelu
	            geographicBoundaryDAO = OracleMWDAOFactory.getGeographicBoundaryDAO();
	            geos = (List<GeographicBoundaryTO>) (List) geographicBoundaryDAO.select(criteria, GeographicBoundaryDAO.SELECT_SQL_LIST, filterExpression, sort, pageIndex, pageSize, rowCount);
	            // DAOException
	        } catch (DAOException e) {
	        	logger.error(null, e);
	        	throw new WebApplicationException(e);
	        } finally {
				// remove() operations should be performed within finally blocks,
				// ensuring their invocation regardless of the execution path of the
				// code.
				MDC.remove("MSISDN");
	        }
			
			// Stopping the context will close all appenders attached to loggers
			// defined by the context and stop any active threads. In
			// web-applications, this code would be invoked from within the
			// contextDestroyed method of ServletContextListener 
			//loggerContext.stop();
			
			return geos.toArray(new GeographicBoundaryTO[geos.size()]);
		}
		
		@GET
		@Path("geographicboundary/{geoId}")
		public GeographicBoundaryTO findGeographicBoundaryByPrimaryKey(@PathParam("geoId") int geoId) throws DAOException {
	        GeographicBoundaryDAO geographicBoundaryDAO = null;
	        GeographicBoundaryTO geographicBoundaryTO = null;
	        GeographicBoundaryTO criteria = new GeographicBoundaryTO();
	        criteria.setGeoId(geoId);

	        try {
	        	geographicBoundaryDAO = OracleMWDAOFactory.getGeographicBoundaryDAO();
	        	geographicBoundaryTO = (GeographicBoundaryTO) geographicBoundaryDAO.findByPrimaryKey(criteria, GeographicBoundaryDAO.FIND_BY_PRIMARY_KEY_SQL);
	        	// DAOException
	        	
	        	if (geographicBoundaryTO == null) {throw new WebApplicationException(Response.Status.NOT_FOUND);
	        	}
	        } catch (DAOException e) {
	        	logger.error(null, e);
	        	throw e; // Rethrow
	        }
	        
			return geographicBoundaryTO;
		}
		
		@POST
		@Consumes("application/json")
//		@ValidateRequest
		public GeographicBoundaryTO create(GeographicBoundaryTO newGeographicBoundaryTO) {
	        GeographicBoundaryDAO geographicBoundaryDAO = null;
	        Object primaryColVal = null;
	        System.out.println("create GeographicBoundary parentId="+newGeographicBoundaryTO.getParentGeoId());
	        try {
	        	geographicBoundaryDAO = OracleMWDAOFactory.getGeographicBoundaryDAO();
	        	primaryColVal = geographicBoundaryDAO.create(newGeographicBoundaryTO, GeographicBoundaryDAO.INSERT_SQL);
	        	newGeographicBoundaryTO.setGeoId(((Integer) primaryColVal).intValue());
	        } 
	        catch (DAOException e) {
	        	logger.error("Error inserting data.", e);
	        	// Construct a new instance with a blank message and default HTTP
	        	// status code of 500
	        	throw new WebApplicationException(e);
	        }
	        System.out.println("create GeographicBoundary newId="+newGeographicBoundaryTO.getGeoId());
			return newGeographicBoundaryTO;
		}
		
		@PUT
		public void update(GeographicBoundaryTO geographicBoundaryTO) {
			
			GeographicBoundaryDAO geographicBoundaryDAO = null;    
	        try {
	        	geographicBoundaryDAO = OracleMWDAOFactory.getGeographicBoundaryDAO();
	        	geographicBoundaryDAO.update(geographicBoundaryTO, GeographicBoundaryDAO.UPDATE_SQL);
	        } 
	        catch (DAOException e) {
	            logger.error("Error updating data.", e);
	            throw new WebApplicationException(e);
	        }
	        resp.setStatus(Response.Status.NO_CONTENT.getStatusCode());
		}
		
		@DELETE
		@Path("geographicboundary/{geoId}")
		public void delete(@PathParam("geoId") int geoId) {
			int sc = Response.Status.NO_CONTENT.getStatusCode();
	        GeographicBoundaryDAO geographicBoundaryDAO = null;
	        GeographicBoundaryTO geographicBoundaryTO = null;
	        GeographicBoundaryTO criteria = new GeographicBoundaryTO();
	        criteria.setGeoId(geoId);
	        
	        try {
	        	geographicBoundaryDAO = OracleMWDAOFactory.getGeographicBoundaryDAO();
	        	geographicBoundaryTO = (GeographicBoundaryTO)geographicBoundaryDAO.findByPrimaryKey(criteria, GeographicBoundaryDAO.FIND_BY_PRIMARY_KEY_SQL);
	        	// DAOException
	        	
	        	if (geographicBoundaryTO != null) {
	        		geographicBoundaryDAO.delete(geographicBoundaryTO, GeographicBoundaryDAO.DELETE_SQL);
	        		// OptimisticLockException, DAOException
	        	} else {
	        		// We are telling the client that the thing we want to delete is
	        		// already gone (410).
	        		sc = Response.Status.GONE.getStatusCode();
	        	}
	        } catch (OptimisticLockException e) {
	        	sc = Response.Status.GONE.getStatusCode();
	        	logger.error("Data shown on form couldn't be saved, because they are changed in the mean time or new version of data was saved by other user. Try to load form again and save the data.", e);
	        } catch (DAOException e) {
	        	logger.error(null, e);
	        	throw new WebApplicationException(e);
	        }

			resp.setStatus(sc);
		}
		
		
}
