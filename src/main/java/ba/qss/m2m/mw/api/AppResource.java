package ba.qss.m2m.mw.api;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.ws.rs.Consumes;
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

import ba.qss.framework.IntValue;
import ba.qss.framework.dataaccess.DAOException;
import ba.qss.framework.dataaccess.TO;
import ba.qss.framework.dataaccess.UserDAO;
import ba.qss.m2m.mw.dao.AppDAO;
import ba.qss.m2m.mw.dao.AppTO;
import ba.qss.m2m.mw.dao.OracleMWDAOFactory;
import ba.qss.m2m.mw.dao.RoutingTableDAO;
import ba.qss.m2m.mw.dao.RoutingTableTO;

@Path("apps")
@Produces("application/json")
public class AppResource {

    private static final String CLASS_NAME = AppResource.class.getName();
    public static final Logger logger = LoggerFactory.getLogger(CLASS_NAME);
    
    @Context
    private HttpServletResponse resp;
    
    @GET
//    @ValidateRequest
    public AppTO[] select(
    		/*@Context UriInfo uriInfo
			,*/@Pattern(regexp="[\\w\\s,]+") @Size(max=50) @DefaultValue("app_name ASC") @QueryParam("sort") String sort // XXX: Sort key expression: ORDER BY sin(i)
			,@Min(value=0, message="{bindingEntity.sort.size}") @Max(100) @DefaultValue("0") @QueryParam("pageIndex") int pageIndex
			,@Min(0) @Max(1000) @DefaultValue("20") @QueryParam("pageSize") int pageSize) {
		AppDAO appDAO = null;
		List<AppTO> apps = null;
		AppTO criteria = new AppTO();
        IntValue rowCount = new IntValue(0);

		try {
			appDAO = OracleMWDAOFactory.getAppDAO();
			apps = (List<AppTO>) (List) appDAO.select(criteria,
            		AppDAO.SELECT_SQL_LIST, null/*filterExpression*/, sort,
            		pageIndex, pageSize, rowCount);
            // DAOException
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw new WebApplicationException(e);
        }
        
    	return apps.toArray(new AppTO[apps.size()]);
    }
    
	@GET
	@Path("app/{appId}")
	public AppTO findAppByPrimaryKey(
			// PathParam (Java EE 6)
			// http://docs.oracle.com/javaee/6/api/javax/ws/rs/PathParam.html
			// The value is URL decoded unless this is disabled using the
			// Encoded annotation.
			@PathParam("appId") int appId)
			throws DAOException {
        AppDAO appDAO = null;
        AppTO appTO = null;
        TO criteria = null;
        
        try {
        	appDAO = OracleMWDAOFactory.getAppDAO();
        	
        	criteria = new AppTO();
        	((AppTO) criteria).setAppId(appId);
        	appTO = (AppTO) appDAO.findByPrimaryKey(criteria, AppDAO.FIND_BY_PRIMARY_KEY_SQL);
        	// DAOException
        	
        	if (appTO == null) {
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
        
		return appTO;
	}
	
	@POST
	@Consumes("application/json")
//	@ValidateRequest
	public AppTO create(@Valid AppTO newAppTO) {
        AppDAO appDAO = null;
        Object primaryColVal = null;

        try {
        	appDAO = OracleMWDAOFactory.getAppDAO();
        	primaryColVal = appDAO.create(newAppTO, AppDAO.INSERT_SQL);
        	// DAOException
        	
        	newAppTO.setAppId(((Integer) primaryColVal).intValue());
        	// ClassCastException
        } catch (DAOException e) {
        	logger.error("Error inserting data.", e);
        	throw new WebApplicationException(e);
        }
		
		return newAppTO;		
	}
	
	@PUT
    public void update(AppTO appTO) {
		int sc = Response.Status.NO_CONTENT.getStatusCode();
		AppDAO appDAO = null;
		
    	try {
    		appDAO = OracleMWDAOFactory.getAppDAO();
    		appDAO.update(appTO, AppDAO.UPDATE_SQL);
    	} catch (DAOException e) {
        	logger.error("Error updating data.", e);
        	// Construct a new instance with a blank message and default HTTP
        	// status code of 500        	
    		throw new WebApplicationException(e);
    	}
    	
    	resp.setStatus(sc);
    }
}
