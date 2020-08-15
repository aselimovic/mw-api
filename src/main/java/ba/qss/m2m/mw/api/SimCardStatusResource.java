package ba.qss.m2m.mw.api;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
import ba.qss.m2m.mw.dao.SimCardStatusDAO;
import ba.qss.m2m.mw.dao.SimCardStatusTO;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;

@Path("simCardStatus")
@Produces("application/json")
public class SimCardStatusResource {
	
	private static final String CLASS_NAME =
			SimCardStatusResource.class.getName();
    

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
	public SimCardStatusTO[] select(

			@QueryParam("filterExpression") String filterExpression 
			,@Size(max=50) @DefaultValue("") @QueryParam("sort") String sort
			,@Min(value=0, message="{simCardStatus.sort.size}") @Max(100) @DefaultValue("0") @QueryParam("pageIndex") int pageIndex
			,@Min(0) @Max(1000) @DefaultValue("20") @QueryParam("pageSize") int pageSize) {
    	SimCardStatusDAO simCardStatusDAO = null;
		List<SimCardStatusTO> simCardStatus = null;
		SimCardStatusTO criteria = new SimCardStatusTO();
        IntValue rowCount = new IntValue(0);
        LoggerContext loggerContext = null;

        
        loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        StatusPrinter.print(loggerContext);
        
		try {
           
			simCardStatusDAO = OracleMWDAOFactory.getSimCardStatusDAO();
			simCardStatus = (List<SimCardStatusTO>) (List) simCardStatusDAO.select(
                    criteria, SimCardStatusDAO.SELECT_SQL_LIST,
                    filterExpression, sort, pageIndex, pageSize,
                    rowCount);
            
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw new WebApplicationException(e);
        }
		
		return simCardStatus.toArray(new SimCardStatusTO[simCardStatus.size()]);
	}
    
    
    @GET
	@Path("simCardStatus/{simCardStatusId}")
	public SimCardStatusTO findSimCardByPrimaryKey(
			
			@PathParam("simCardStatusId") int simCardStatusId)
			throws DAOException {
    	SimCardStatusDAO simCardStatusDAO = null;
    	SimCardStatusTO simCardStatusTO = null;
    	SimCardStatusTO criteria = new SimCardStatusTO();
    	criteria.setSimCardStatusId(simCardStatusId);

        try {
        	simCardStatusDAO = OracleMWDAOFactory.getSimCardStatusDAO();
        	simCardStatusTO = (SimCardStatusTO)simCardStatusDAO.findByPrimaryKey(criteria, SimCardStatusDAO.FIND_BY_PRIMARY_KEY_SQL);
        	
        	
        	if (simCardStatusTO == null) {
        		
        		throw new WebApplicationException(Response.Status.NOT_FOUND);
        	}
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw e;
        }
        
		return simCardStatusTO;
	}
    
    @POST
	@Consumes("application/json")
//	@ValidateRequest
	public SimCardStatusTO create(@Valid SimCardStatusTO newSimCardStatusTO) {
    	SimCardStatusDAO simCardStatusDAO = null;
        Object primaryColVal = null;

        try {
        	simCardStatusDAO = OracleMWDAOFactory.getSimCardStatusDAO();
        	primaryColVal = simCardStatusDAO.create(newSimCardStatusTO,
        			SimCardStatusDAO.INSERT_SQL);

        	newSimCardStatusTO.setSimCardStatusId(
        			((Integer) primaryColVal).intValue());
        			
        } catch (DAOException e) {
        	logger.error("Error inserting data.", e);
        	
        	throw new WebApplicationException(e);
        }
        
		return newSimCardStatusTO;
	}

}
