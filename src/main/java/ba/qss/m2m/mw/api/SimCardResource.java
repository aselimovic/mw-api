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

import ba.qss.framework.IntValue;
import ba.qss.framework.dataaccess.DAOException;
import ba.qss.m2m.mw.dao.OracleMWDAOFactory;
import ba.qss.m2m.mw.dao.SimCardDAO;
import ba.qss.m2m.mw.dao.SimCardTO;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;

@Path("simCard")
@Produces("application/json")
public class SimCardResource {
	
	private static final String CLASS_NAME =
			SimCardResource.class.getName();
    

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
	public SimCardTO[] select(

			@QueryParam("filterExpression") String filterExpression 
			,@Size(max=50) @DefaultValue("") @QueryParam("sort") String sort
			,@Min(value=0, message="{simCard.sort.size}") @Max(100) @DefaultValue("0") @QueryParam("pageIndex") int pageIndex
			,@Min(0) @Max(1000) @DefaultValue("20") @QueryParam("pageSize") int pageSize) {
    	SimCardDAO simCardDAO = null;
		List<SimCardTO> simCard = null;
		SimCardTO criteria = new SimCardTO();
        IntValue rowCount = new IntValue(0);
        LoggerContext loggerContext = null;

        
        loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        StatusPrinter.print(loggerContext);
        
		try {
           
			simCardDAO = OracleMWDAOFactory.getSimCardDAO();
			simCard = (List<SimCardTO>) (List) simCardDAO.select(
                    criteria, SimCardDAO.SELECT_SQL_LIST,
                    filterExpression, sort, pageIndex, pageSize,
                    rowCount);
            
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw new WebApplicationException(e);
        }
		
		return simCard.toArray(new SimCardTO[simCard.size()]);
	}
    
    
    @GET
	@Path("simCard/{simCardId}")
	public SimCardTO findSimCardByPrimaryKey(
			
			@PathParam("simCardId") int simCardId)
			throws DAOException {
    	SimCardDAO simCardDAO = null;
    	SimCardTO simCardTO = null;
    	SimCardTO criteria = new SimCardTO();
    	criteria.setSimCardId(simCardId);

        try {
        	simCardDAO = OracleMWDAOFactory.getSimCardDAO();
        	simCardTO = (SimCardTO)simCardDAO.findByPrimaryKey(criteria, SimCardDAO.FIND_BY_PRIMARY_KEY_SQL);
        	
        	
        	if (simCardTO == null) {
        		
        		throw new WebApplicationException(Response.Status.NOT_FOUND);
        	}
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw e;
        }
        
		return simCardTO;
	}
    
    @POST
	@Consumes("application/json")
//	@ValidateRequest
	public SimCardTO create(@Valid SimCardTO newSimCardTO) {
    	SimCardDAO simCardDAO = null;
        Object primaryColVal = null;

        try {
        	simCardDAO = OracleMWDAOFactory.getSimCardDAO();
        	primaryColVal = simCardDAO.create(newSimCardTO,
        			SimCardDAO.INSERT_SQL);

        	newSimCardTO.setSimCardId(
        			((Integer) primaryColVal).intValue());
        			
        } catch (DAOException e) {
        	logger.error("Error inserting data.", e);
        	
        	throw new WebApplicationException(e);
        }
        
		return newSimCardTO;
	}
    
    @PUT
    public void update(SimCardTO simCardTO) {

    	SimCardDAO simCardDAO = null;
    	try {
    		simCardDAO = OracleMWDAOFactory.getSimCardDAO();
    		int result = simCardDAO.update(simCardTO, SimCardDAO.UPDATE_SQL);
    		
    	} catch (DAOException e) {
    		logger.error("Error inserting data.", e);

    		throw new WebApplicationException(e);
    	}
    	
    	resp.setStatus(Response.Status.NO_CONTENT.getStatusCode());

    }
    
    
    @DELETE
	@Path("simCard/{simCardId}")
	public void delete(@PathParam("simCardId") int simCardId) {
		int sc = Response.Status.NO_CONTENT.getStatusCode();
		SimCardDAO simCardDAO = null;
		SimCardTO simCardTO = null;
		SimCardTO criteria = new SimCardTO();
		criteria.setSimCardId(simCardId);
        
        try {
        	simCardDAO = OracleMWDAOFactory.getSimCardDAO();
        	
        	simCardTO = (SimCardTO)simCardDAO.findByPrimaryKey(criteria,SimCardDAO.FIND_BY_PRIMARY_KEY_SQL);
        	
        	if (simCardTO != null) {
        		simCardDAO.delete(simCardTO, SimCardDAO.DELETE_SQL);
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
