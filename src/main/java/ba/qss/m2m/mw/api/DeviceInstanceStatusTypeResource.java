package ba.qss.m2m.mw.api;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
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
import ba.qss.m2m.mw.dao.DeviceInstanceStatusTypeDAO;
import ba.qss.m2m.mw.dao.DeviceInstanceStatusTypeTO;
import ba.qss.m2m.mw.dao.DeviceTypeTO;
import ba.qss.m2m.mw.dao.OracleMWDAOFactory;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;

@Path("deviceInstanceStatusTypes")
@Produces("application/json")
public class DeviceInstanceStatusTypeResource {

	private static final String CLASS_NAME =
			DeviceInstanceStatusTypeResource.class.getName();
    

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
	public DeviceInstanceStatusTypeTO[] select(

			@QueryParam("filterExpression") String filterExpression 
			,@Size(max=50) @DefaultValue("") @QueryParam("sort") String sort
			,@Min(value=0, message="{deviceInstanceStatusType.sort.size}") @Max(100) @DefaultValue("0") @QueryParam("pageIndex") int pageIndex
			,@Min(0) @Max(1000) @DefaultValue("20") @QueryParam("pageSize") int pageSize) {
    	DeviceInstanceStatusTypeDAO deviceInstanceStatusTypeDAO = null;
		List<DeviceInstanceStatusTypeTO> deviceInstanceStatusTypes = null;
		DeviceInstanceStatusTypeTO criteria = new DeviceInstanceStatusTypeTO();
        IntValue rowCount = new IntValue(0);
        LoggerContext loggerContext = null;

        
        loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        StatusPrinter.print(loggerContext);

		try {
           
			deviceInstanceStatusTypeDAO = OracleMWDAOFactory.getDeviceInstanceStatusTypeDAO();
			deviceInstanceStatusTypes = (List<DeviceInstanceStatusTypeTO>) (List) deviceInstanceStatusTypeDAO.select(
                    criteria, DeviceInstanceStatusTypeDAO.SELECT_SQL_LIST,
                    filterExpression, sort, pageIndex, pageSize,
                    rowCount);
            
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw new WebApplicationException(e);
        }
		
		return deviceInstanceStatusTypes.toArray(new DeviceInstanceStatusTypeTO[deviceInstanceStatusTypes.size()]);
	}
    
    
    @GET
	@Path("deviceInstanceStatusType/{deviceInstanceStatusTypeId}")
	public DeviceInstanceStatusTypeTO findDeviceInstanceStatusTypeByPrimaryKey(
			
			@PathParam("deviceInstanceStatusTypeId") int deviceInstanceStatusTypeId)
			throws DAOException {
    	DeviceInstanceStatusTypeDAO deviceInstanceStatusTypeDAO = null;
    	DeviceInstanceStatusTypeTO deviceInstanceStatusTypeTO = null;
    	DeviceInstanceStatusTypeTO criteria = new DeviceInstanceStatusTypeTO();
        criteria.setDeviceInstanceStatusTypeId(deviceInstanceStatusTypeId);
        
        try {
        	deviceInstanceStatusTypeDAO = OracleMWDAOFactory.getDeviceInstanceStatusTypeDAO();
        	deviceInstanceStatusTypeTO = (DeviceInstanceStatusTypeTO)deviceInstanceStatusTypeDAO.findByPrimaryKey(criteria, DeviceInstanceStatusTypeDAO.FIND_BY_PRIMARY_KEY_SQL);
        	
        	
        	if (deviceInstanceStatusTypeTO == null) {
        		
        		throw new WebApplicationException(Response.Status.NOT_FOUND);
        	}
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw e;
        }
        
		return deviceInstanceStatusTypeTO;
	}
    
}
