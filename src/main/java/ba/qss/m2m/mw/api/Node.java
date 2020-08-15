package ba.qss.m2m.mw.api;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
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
import ba.qss.m2m.mw.dao.DeviceTypeDAO;
import ba.qss.m2m.mw.dao.DeviceTypeTO;
import ba.qss.m2m.mw.dao.OracleMWDAOFactory;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;

@Path("http-probe")
@Produces("application/json")
public class Node {
	
	private static final String CLASS_NAME =
			Node.class.getName();
    

    public static final Logger logger = LoggerFactory.getLogger(CLASS_NAME);

    @Context
    private HttpServletResponse resp;
    
    @Context
    public void setServletContext(ServletContext context) {
    	String razvoj = null;

    	razvoj = context.getInitParameter("Razvoj");
    }
    
    @GET
	public String select() {
        
		try {
			DeviceTypeDAO deviceTypeDAO = null;
			DeviceTypeTO deviceTypeTO = null;
			DeviceTypeTO criteria = new DeviceTypeTO();
			criteria.setDeviceTypeId(1);
	        
			deviceTypeDAO = OracleMWDAOFactory.getDeviceTypeDAO();
			deviceTypeTO = (DeviceTypeTO)deviceTypeDAO.findByPrimaryKey(criteria, DeviceTypeDAO.FIND_BY_PRIMARY_KEY_SQL);
			
        } catch (Exception e) {
        	throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
		
		return "";
	}

}
