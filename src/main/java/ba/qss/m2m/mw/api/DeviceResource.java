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
import ba.qss.m2m.mw.dao.BindingEntityDAO;
import ba.qss.m2m.mw.dao.BindingEntityTO;
import ba.qss.m2m.mw.dao.DeviceDAO;
import ba.qss.m2m.mw.dao.DeviceTO;
import ba.qss.m2m.mw.dao.OracleMWDAOFactory;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;

@Path("device")
@Produces("application/json")
public class DeviceResource {
	
	private static final String CLASS_NAME =
			DeviceResource.class.getName();
    

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
	public DeviceTO[] select(

			@QueryParam("filterExpression") String filterExpression 
			,@Size(max=50) @DefaultValue("") @QueryParam("sort") String sort
			,@Min(value=0, message="{device.sort.size}") @Max(100) @DefaultValue("0") @QueryParam("pageIndex") int pageIndex
			,@Min(0) @Max(1000) @DefaultValue("20") @QueryParam("pageSize") int pageSize) {
    	DeviceDAO deviceDAO = null;
		List<DeviceTO> device = null;
		DeviceTO criteria = new DeviceTO();
        IntValue rowCount = new IntValue(0);
        LoggerContext loggerContext = null;

        
        /*loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        StatusPrinter.print(loggerContext);*/
        
		try {
           
			deviceDAO = OracleMWDAOFactory.getDeviceDAO();
			device = (List<DeviceTO>) (List) deviceDAO.select(
                    criteria, DeviceDAO.SELECT_SQL_LIST,
                    filterExpression, sort, pageIndex, pageSize,
                    rowCount);
            
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw new WebApplicationException(e);
        }
		
		return device.toArray(new DeviceTO[device.size()]);
	}
    
    
    @GET
	@Path("device/{deviceId}")
	public DeviceTO findDeviceByPrimaryKey(
			
			@PathParam("deviceId") int deviceId)
			throws DAOException {
    	DeviceDAO deviceDAO = null;
    	DeviceTO deviceTO = null;
    	DeviceTO criteria = new DeviceTO();
    	criteria.setDeviceId(deviceId);

        try {
        	deviceDAO = OracleMWDAOFactory.getDeviceDAO();
        	deviceTO = (DeviceTO)deviceDAO.findByPrimaryKey(criteria, DeviceDAO.FIND_BY_PRIMARY_KEY_SQL);
        	
        	
        	if (deviceTO == null) {
        		
        		throw new WebApplicationException(Response.Status.NOT_FOUND);
        	}
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw e;
        }
        
		return deviceTO;
	}
    
    
    @POST
	@Consumes("application/json")
//	@ValidateRequest
	public DeviceTO create(@Valid DeviceTO newDeviceTO) {
    	DeviceDAO deviceDAO = null;
        Object primaryColVal = null;

        try {
        	deviceDAO = OracleMWDAOFactory.getDeviceDAO();
        	primaryColVal = deviceDAO.create(newDeviceTO,
        			DeviceDAO.INSERT_SQL);

        	newDeviceTO.setDeviceId(
        			((Integer) primaryColVal).intValue());
        			
        } catch (DAOException e) {
        	logger.error("Error inserting data.", e);
        	
        	throw new WebApplicationException(e);
        }
        
		return newDeviceTO;
	}
    
    @PUT
    public void update(DeviceTO deviceTO) {

    	DeviceDAO deviceDAO = null;
    	try {
    		deviceDAO = OracleMWDAOFactory.getDeviceDAO();
    		int result = deviceDAO.update(deviceTO, DeviceDAO.UPDATE_SQL);
    		
    	} catch (DAOException e) {
    		logger.error("Error inserting data.", e);

    		throw new WebApplicationException(e);
    	}
    	
    	resp.setStatus(Response.Status.NO_CONTENT.getStatusCode());

    }
    
    
    @DELETE
	@Path("device/{deviceId}")
	public void delete(@PathParam("deviceId") int deviceId) {
		int sc = Response.Status.NO_CONTENT.getStatusCode();
        DeviceDAO deviceDAO = null;
        DeviceTO deviceTO = null;
		DeviceTO criteria = new DeviceTO();
		criteria.setDeviceId(deviceId);
        
        try {
        	deviceDAO = OracleMWDAOFactory.getDeviceDAO();
        	
        	deviceTO = (DeviceTO)deviceDAO.findByPrimaryKey(criteria,DeviceDAO.FIND_BY_PRIMARY_KEY_SQL);
        	
        	if (deviceTO != null) {
        		deviceDAO.delete(deviceTO, DeviceDAO.DELETE_SQL);
        	} else {
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
