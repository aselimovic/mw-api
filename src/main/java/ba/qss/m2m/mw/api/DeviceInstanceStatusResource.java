package ba.qss.m2m.mw.api;

import java.util.List;

import javax.servlet.ServletContext;
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
import org.slf4j.MDC;

import ba.qss.framework.IntValue;
import ba.qss.framework.dataaccess.DAOException;
import ba.qss.m2m.mw.dao.DeviceInstanceStatusDAO;
import ba.qss.m2m.mw.dao.DeviceInstanceStatusTO;
import ba.qss.m2m.mw.dao.OracleMWDAOFactory;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;

@Path("deviceInstanceStatus")
@Produces("application/json")
public class DeviceInstanceStatusResource {
	
	private static final String CLASS_NAME =
			DeviceInstanceStatusResource.class.getName();
    

    public static final Logger logger = LoggerFactory.getLogger(CLASS_NAME);

    @Context
    private HttpServletResponse resp;
    
    @Context
    public void setServletContext(ServletContext context) {
    	String razvoj = null;

    	razvoj = context.getInitParameter("Razvoj");
    }
    
    @GET
//    @ValidateRequest
	public DeviceInstanceStatusTO[] select(

			@QueryParam("filterExpression") String filterExpression 
			,@Size(max=50) @DefaultValue("") @QueryParam("sort") String sort
			,@Min(value=0, message="{deviceInstanceStatus.sort.size}") @Max(100) @DefaultValue("0") @QueryParam("pageIndex") int pageIndex
			,@Min(0) @Max(1000) @DefaultValue("20") @QueryParam("pageSize") int pageSize) {
    	DeviceInstanceStatusDAO deviceInstanceStatusDAO = null;
		List<DeviceInstanceStatusTO> deviceInstanceStatus = null;
		DeviceInstanceStatusTO criteria = new DeviceInstanceStatusTO();
        IntValue rowCount = new IntValue(0);
        LoggerContext loggerContext = null;

        
        loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        StatusPrinter.print(loggerContext);

		try {
           
			deviceInstanceStatusDAO = OracleMWDAOFactory.getDeviceInstanceStatusDAO();
			deviceInstanceStatus = (List<DeviceInstanceStatusTO>) (List) deviceInstanceStatusDAO.select(
                    criteria, DeviceInstanceStatusDAO.SELECT_SQL_LIST,
                    filterExpression, sort, pageIndex, pageSize,
                    rowCount);
            
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw new WebApplicationException(e);
        }
		
		return deviceInstanceStatus.toArray(new DeviceInstanceStatusTO[deviceInstanceStatus.size()]);
	}
    
    
    @GET
	@Path("deviceInstanceStatus/{deviceInstanceStatusId}")
	public DeviceInstanceStatusTO findDeviceInstanceStatusByPrimaryKey(
			
			@PathParam("deviceInstanceStatusId") int deviceInstanceStatusId)
			throws DAOException {
    	DeviceInstanceStatusDAO deviceInstanceStatusDAO = null;
    	DeviceInstanceStatusTO deviceInstanceStatusTO = null;
    	DeviceInstanceStatusTO criteria = new DeviceInstanceStatusTO();
    	criteria.setDeviceInstanceStatusId(deviceInstanceStatusId);

        try {
        	deviceInstanceStatusDAO = OracleMWDAOFactory.getDeviceInstanceStatusDAO();
        	deviceInstanceStatusTO = (DeviceInstanceStatusTO)deviceInstanceStatusDAO.findByPrimaryKey(criteria, DeviceInstanceStatusDAO.FIND_BY_PRIMARY_KEY_SQL);
        	
        	
        	if (deviceInstanceStatusTO == null) {
        		
        		throw new WebApplicationException(Response.Status.NOT_FOUND);
        	}
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw e;
        }
        
		return deviceInstanceStatusTO;
	}
    
    
    @POST
	@Consumes("application/json")
//	@ValidateRequest
	public DeviceInstanceStatusTO create(@Valid DeviceInstanceStatusTO newDeviceInstanceStatusTO) {
    	DeviceInstanceStatusDAO deviceInstanceStatusDAO = null;
        Object primaryColVal = null;

        try {
        	deviceInstanceStatusDAO = OracleMWDAOFactory.getDeviceInstanceStatusDAO();
        	primaryColVal = deviceInstanceStatusDAO.create(newDeviceInstanceStatusTO,
        			DeviceInstanceStatusDAO.INSERT_SQL);

        	newDeviceInstanceStatusTO.setDeviceInstanceStatusId(
        			((Integer) primaryColVal).intValue());
        			
        } catch (DAOException e) {
        	logger.error("Error inserting data.", e);
        	
        	throw new WebApplicationException(e);
        }
        
		return newDeviceInstanceStatusTO;
	}
    
    @PUT
    public void update(DeviceInstanceStatusTO deviceInstanceStatusTO) {

    	DeviceInstanceStatusDAO deviceInstanceStatusDAO = null;
    	try {
    		deviceInstanceStatusDAO = OracleMWDAOFactory.getDeviceInstanceStatusDAO();
    		int result = deviceInstanceStatusDAO.update(deviceInstanceStatusTO, DeviceInstanceStatusDAO.UPDATE_SQL);
    		
    	} catch (DAOException e) {
    		logger.error("Error inserting data.", e);

    		throw new WebApplicationException(e);
    	}
    	
    	resp.setStatus(Response.Status.NO_CONTENT.getStatusCode());

    }

}
