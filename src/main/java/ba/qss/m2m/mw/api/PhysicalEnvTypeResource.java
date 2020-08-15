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

//import org.jboss.resteasy.spi.validation.ValidateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ba.qss.framework.IntValue;
import ba.qss.framework.dataaccess.DAOException;
import ba.qss.m2m.mw.dao.OracleMWDAOFactory;
import ba.qss.m2m.mw.dao.PhysicalEnvTypeDAO;
import ba.qss.m2m.mw.dao.PhysicalEnvTypeTO;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;

@Path("physicalEnvType")
@Produces("application/json")
public class PhysicalEnvTypeResource {
	
	private static final String CLASS_NAME =
			PhysicalEnvTypeResource.class.getName();
    

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
    public PhysicalEnvTypeTO[] select(

			@QueryParam("filterExpression") String filterExpression 
			,@Size(max=50) @DefaultValue("") @QueryParam("sort") String sort
			,@Min(value=0, message="{physicalEnvType.sort.size}") @Max(100) @DefaultValue("0") @QueryParam("pageIndex") int pageIndex
			,@Min(0) @Max(1000) @DefaultValue("20") @QueryParam("pageSize") int pageSize) {
    	PhysicalEnvTypeDAO physicalEnvTypeDAO = null;
		List<PhysicalEnvTypeTO> physicalEnvTypes = null;
		PhysicalEnvTypeTO criteria = new PhysicalEnvTypeTO();
        IntValue rowCount = new IntValue(0);
        LoggerContext loggerContext = null;

        loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        StatusPrinter.print(loggerContext);

		try {
           
			physicalEnvTypeDAO = OracleMWDAOFactory.getPhysicalEnvTypeDAO();
			physicalEnvTypes = (List<PhysicalEnvTypeTO>) (List) physicalEnvTypeDAO.select(
                    criteria, PhysicalEnvTypeDAO.SELECT_SQL_LIST,
                    filterExpression, sort, pageIndex, pageSize,
                    rowCount);
            
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw new WebApplicationException(e);
        }
		
		return physicalEnvTypes.toArray(new PhysicalEnvTypeTO[physicalEnvTypes.size()]);
	}

}
