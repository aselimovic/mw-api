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

import ba.qss.framework.IntValue;
import ba.qss.framework.dataaccess.DAOException;
import ba.qss.m2m.mw.dao.OracleMWDAOFactory;
import ba.qss.m2m.mw.dao.SimCardStatusTypeDAO;
import ba.qss.m2m.mw.dao.SimCardStatusTypeTO;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;

@Path("simCardStatusType")
@Produces("application/json")
public class SimCardStatusTypeResource {
	
	private static final String CLASS_NAME =
			SimCardStatusTypeResource.class.getName();
    

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
	public SimCardStatusTypeTO[] select(

			@QueryParam("filterExpression") String filterExpression 
			,@Size(max=50) @DefaultValue("") @QueryParam("sort") String sort
			,@Min(value=0, message="{simCardStatusType.sort.size}") @Max(100) @DefaultValue("0") @QueryParam("pageIndex") int pageIndex
			,@Min(0) @Max(1000) @DefaultValue("20") @QueryParam("pageSize") int pageSize) {
    	SimCardStatusTypeDAO simCardStatusTypeDAO = null;
		List<SimCardStatusTypeTO> simCardStatusType = null;
		SimCardStatusTypeTO criteria = new SimCardStatusTypeTO();
        IntValue rowCount = new IntValue(0);
        LoggerContext loggerContext = null;

        
        loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        StatusPrinter.print(loggerContext);
        
		try {
           
			simCardStatusTypeDAO = OracleMWDAOFactory.getSimCardStatusTypeDAO();
			simCardStatusType = (List<SimCardStatusTypeTO>) (List) simCardStatusTypeDAO.select(
                    criteria, SimCardStatusTypeDAO.SELECT_SQL_LIST,
                    filterExpression, sort, pageIndex, pageSize,
                    rowCount);
            
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw new WebApplicationException(e);
        }
		
		return simCardStatusType.toArray(new SimCardStatusTypeTO[simCardStatusType.size()]);
	}
    
    
    @GET
	@Path("simCardStatusType/{simCardStatusTypeId}")
	public SimCardStatusTypeTO findSimCardStatusTypeByPrimaryKey(
			
			@PathParam("simCardStatusTypeId") int simCardStatusTypeId)
			throws DAOException {
    	SimCardStatusTypeDAO simCardStatusTypeDAO = null;
    	SimCardStatusTypeTO simCardStatusTypeTO = null;
    	SimCardStatusTypeTO criteria = new SimCardStatusTypeTO();
    	criteria.setSimCardStatusTypeId(simCardStatusTypeId);

        try {
        	simCardStatusTypeDAO = OracleMWDAOFactory.getSimCardStatusTypeDAO();
        	simCardStatusTypeTO = (SimCardStatusTypeTO)simCardStatusTypeDAO.findByPrimaryKey(criteria, SimCardStatusTypeDAO.FIND_BY_PRIMARY_KEY_SQL);
        	
        	
        	if (simCardStatusTypeTO == null) {
        		
        		throw new WebApplicationException(Response.Status.NOT_FOUND);
        	}
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw e;
        }
        
		return simCardStatusTypeTO;
	}

}
