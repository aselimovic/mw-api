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
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

//import org.jboss.resteasy.spi.validation.ValidateRequest;
import org.slf4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;
import ba.qss.framework.dataaccess.DAOException;
import ba.qss.framework.IntValue;
import ba.qss.m2m.mw.dao.BindingEntityDAO;
import ba.qss.m2m.mw.dao.BindingEntityTO;
import ba.qss.m2m.mw.dao.OracleMWDAOFactory;

// Path (Java EE 6 )
// http://docs.oracle.com/javaee/6/api/javax/ws/rs/Path.html
// For an annotated class the base URI is the application path. For the purposes
// of absolutizing a path against the base URI, a leading '/' in a path is
// ignored and base URIs are treated as if they ended in '/'.
@Path("bindingEntities")
// Method level annotations override a class level annotation.
@Produces("application/json")
public class BindingEntityResource {

    private static final String CLASS_NAME =
    		BindingEntityResource.class.getName();
    
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
//    @Produces({"application/xml","application/json"})
	// Integrating Bean Validation with JAX-RS in Java EE 6 » samaxes
	// http://www.samaxes.com/2013/01/beanvalidation-with-jaxrs-in-javaee6/
//	@ValidateRequest
	public BindingEntityTO[] select(
			// Context (Java EE 6)
			// http://docs.oracle.com/javaee/6/api/javax/ws/rs/core/Context.html
			// See Also: Application, UriInfo, Request, HttpHeaders, SecurityContext, Providers
			/*@Context UriInfo uriInfo
			,*/@Pattern(regexp="[\\w\\s,]+") @Size(max=50) @DefaultValue("binding_entity_id ASC") @QueryParam("sort") String sort // XXX: Sort key expression: ORDER BY sin(i)
			,@Min(value=0, message="{bindingEntity.sort.size}") @Max(100) @DefaultValue("0") @QueryParam("pageIndex") int pageIndex
			,@Min(0) @Max(1000) @DefaultValue("20") @QueryParam("pageSize") int pageSize) {
		BindingEntityDAO bindingEntityDAO = null;
		List<BindingEntityTO> bindingEntities = null;
        BindingEntityTO criteria = new BindingEntityTO();
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
            bindingEntityDAO = OracleMWDAOFactory.getBindingEntityDAO();
            bindingEntities = (List<BindingEntityTO>) (List) bindingEntityDAO.select(
                    criteria, BindingEntityDAO.SELECT_SQL_LIST,
                    null/*filterExpression*/, sort, pageIndex, pageSize,
                    rowCount);
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
		
		return bindingEntities.toArray(new BindingEntityTO[bindingEntities.size()]);
	}
	
	@GET
	// RFC 5234 - Augmented BNF for Syntax Specifications: ABNF
	// http://tools.ietf.org/html/rfc5234
	//
	// param = "{" *WSP name *WSP [ ":" *WSP regex *WSP ] "}"
	// name = (ALPHA / DIGIT / "_")*(ALPHA / DIGIT / "." / "_" / "-" ) ; \w[\w\.-]*
	// regex = *( nonbrace / "{" *nonbrace "}" ) ; where nonbrace is any char other than "{" and "}"
	@Path("bindingEntity/{bindingEntityId}")
	public BindingEntityTO findBindingEntityByPrimaryKey(
			// PathParam (Java EE 6)
			// http://docs.oracle.com/javaee/6/api/javax/ws/rs/PathParam.html
			// The value is URL decoded unless this is disabled using the
			// Encoded annotation.
			@PathParam("bindingEntityId") int bindingEntityId)
			throws DAOException {
        BindingEntityDAO bindingEntityDAO = null;
        BindingEntityTO bindingEntityTO = null;

        try {
        	bindingEntityDAO = OracleMWDAOFactory.getBindingEntityDAO();
        	bindingEntityTO = bindingEntityDAO.findBindingEntityByPrimaryKey(
        			bindingEntityId);
        	// DAOException
        	
        	if (bindingEntityTO == null) {
        		// RESTful Java with JAX-RS 2.0, Second Edition (O'Reilly
        		// Media) - Chapter 7: Server Responses and Exception Handling -
        		// Exception Hierarchy
        		// Instead of creating an instance of WebApplicationException
        		// and initializing it with the status code 404, you can use
        		// javax.ws.rs.NotFoundException.
        		//java.lang.ClassNotFoundException: javax.ws.rs.NotFoundException from [Module "deployment.mw-api.war:main" from Service Module Loader]
        		throw new /*NotFoundException()*/WebApplicationException(Response.Status.NOT_FOUND);
        	}
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw e; // Rethrow
        }
        
		return bindingEntityTO;
	}
	
	@POST
	@Consumes("application/json")
//	@ValidateRequest
	public BindingEntityTO create(@Valid BindingEntityTO newBindingEntityTO) {
        BindingEntityDAO bindingEntityDAO = null;
        Object primaryColVal = null;

        try {
        	bindingEntityDAO = OracleMWDAOFactory.getBindingEntityDAO();
        	primaryColVal = bindingEntityDAO.create(newBindingEntityTO,
        			BindingEntityDAO.INSERT_SQL);
        	// DAOException
        	
        	// RESTful Java with JAX-RS 2.0, Second Edition (O'Reilly Media) -
        	// Example ex06_2: Creating a Content Handler - The Resource Class
        	newBindingEntityTO.setBindingEntityId(
        			((Integer) primaryColVal).intValue());
        			// ClassCastException
        } catch (DAOException e) {
        	logger.error("Error inserting data.", e);
        	// Construct a new instance with a blank message and default HTTP
        	// status code of 500
        	throw new WebApplicationException(e);
        }
        
		return newBindingEntityTO;
	}
	
	@PUT
	@Path("bindingEntity/{bindingEntityId}")
	public void update(@PathParam("bindingEntityId") int bindingEntityId,
			BindingEntityTO bindingEntityTO) {
		// RESTful Java with JAX-RS 2.0, Second Edition (O'Reilly Media) -
		// Chapter 7: Server Responses and Exception Handling
		//
		// If the JAX-RS resource method’s return type is void, a response code
		// of 204, “No Content,” is returned. This is the case with our update()
		// and delete() methods.
		resp.setStatus(Response.Status.NO_CONTENT.getStatusCode());
	}
	
	@DELETE
	@Path("bindingEntity/{bindingEntityId}")
	public void delete(@PathParam("bindingEntityId") int bindingEntityId) {
		int sc = Response.Status.NO_CONTENT.getStatusCode();
        BindingEntityDAO bindingEntityDAO = null;
        BindingEntityTO bindingEntityTO = null;
		
        try {
        	bindingEntityDAO = OracleMWDAOFactory.getBindingEntityDAO();
        	bindingEntityTO = bindingEntityDAO.findBindingEntityByPrimaryKey(
        			bindingEntityId);
        	// DAOException
        	
        	if (bindingEntityTO != null) {
        		bindingEntityDAO.delete(bindingEntityTO,
        				BindingEntityDAO.DELETE_SQL);
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
