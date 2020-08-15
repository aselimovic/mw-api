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
import org.slf4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;
import ba.qss.framework.dataaccess.DAOException;
import ba.qss.framework.IntValue;
import ba.qss.m2m.mw.dao.DeviceTypeDAO;
import ba.qss.m2m.mw.dao.DeviceTypeTO;
import ba.qss.m2m.mw.dao.OracleMWDAOFactory;
import ba.qss.m2m.mw.dao.PartyTO;

@Path("deviceTypes")
@Produces("application/json")
public class DeviceTypeResource {
	
	private static final String CLASS_NAME =
			DeviceTypeResource.class.getName();
    

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
	public DeviceTypeTO[] select(

			@QueryParam("filterExpression") String filterExpression 
			,@Size(max=50) @DefaultValue("") @QueryParam("sort") String sort
			,@Min(value=0, message="{deviceType.sort.size}") @Max(100) @DefaultValue("0") @QueryParam("pageIndex") int pageIndex
			,@Min(0) @Max(1000) @DefaultValue("20") @QueryParam("pageSize") int pageSize) {
		DeviceTypeDAO deviceTypeDAO = null;
		List<DeviceTypeTO> deviceTypes = null;
		DeviceTypeTO criteria = new DeviceTypeTO();
        IntValue rowCount = new IntValue(0);
        LoggerContext loggerContext = null;

        
        loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        StatusPrinter.print(loggerContext);

		try {
           
			deviceTypeDAO = OracleMWDAOFactory.getDeviceTypeDAO();
			deviceTypes = (List<DeviceTypeTO>) (List) deviceTypeDAO.select(
                    criteria, DeviceTypeDAO.SELECT_SQL_LIST,
                    filterExpression, sort, pageIndex, pageSize,
                    rowCount);
            
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw new WebApplicationException(e);
        }
		
		return deviceTypes.toArray(new DeviceTypeTO[deviceTypes.size()]);
	}
    
    
    @GET
	@Path("deviceType/{deviceTypeId}")
	public DeviceTypeTO findDeviceTypeByPrimaryKey(
			
			@PathParam("deviceTypeId") int deviceTypeId)
			throws DAOException {
    	DeviceTypeDAO deviceTypeDAO = null;
        DeviceTypeTO deviceTypeTO = null;
        DeviceTypeTO criteria = new DeviceTypeTO();
        criteria.setDeviceTypeId(deviceTypeId);
        
        try {
        	deviceTypeDAO = OracleMWDAOFactory.getDeviceTypeDAO();
        	deviceTypeTO = (DeviceTypeTO)deviceTypeDAO.findByPrimaryKey(criteria, DeviceTypeDAO.FIND_BY_PRIMARY_KEY_SQL);
        	
        	
        	if (deviceTypeTO == null) {
        		
        		throw new WebApplicationException(Response.Status.NOT_FOUND);
        	}
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw e;
        }
        
		return deviceTypeTO;
	}
    

}
