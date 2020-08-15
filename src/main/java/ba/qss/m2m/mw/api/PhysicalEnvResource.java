package ba.qss.m2m.mw.api;

import java.util.List;

import javax.persistence.OptimisticLockException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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
import ba.qss.m2m.mw.dao.PhysicalEnvDAO;
import ba.qss.m2m.mw.dao.PhysicalEnvTO;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;

@Path("physicalEnv")
@Produces("application/json")
public class PhysicalEnvResource {
	
	private static final String CLASS_NAME =
			PhysicalEnvResource.class.getName();
    

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
    public PhysicalEnvTO[] select(

			@QueryParam("filterExpression") String filterExpression 
			,@Size(max=50) @DefaultValue("") @QueryParam("sort") String sort
			,@Min(value=0, message="{physicalEnv.sort.size}") @Max(100) @DefaultValue("0") @QueryParam("pageIndex") int pageIndex
			,@Min(0) @Max(1000) @DefaultValue("20") @QueryParam("pageSize") int pageSize) {
    	PhysicalEnvDAO physicalEnvDAO = null;
		List<PhysicalEnvTO> physicalEnvs = null;
		PhysicalEnvTO criteria = new PhysicalEnvTO();
        IntValue rowCount = new IntValue(0);
        LoggerContext loggerContext = null;

        loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        StatusPrinter.print(loggerContext);

		try {
           
			physicalEnvDAO = OracleMWDAOFactory.getPhysicalEnvDAO();
			physicalEnvs = (List<PhysicalEnvTO>) (List) physicalEnvDAO.select(
                    criteria, PhysicalEnvDAO.SELECT_SQL_LIST,
                    filterExpression, sort, pageIndex, pageSize,
                    rowCount);
            
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw new WebApplicationException(e);
        }
		
		return physicalEnvs.toArray(new PhysicalEnvTO[physicalEnvs.size()]);
	}
    
    @GET
	@Path("physicalEnv/{physicalEnvId}")
	public PhysicalEnvTO findPhysicalEnvByPrimaryKey(
			
			@PathParam("physicalEnvId") int physicalEnvId)
			throws DAOException {
    	PhysicalEnvDAO physicalEnvDAO = null;
    	PhysicalEnvTO physicalEnvTO = null;
    	PhysicalEnvTO criteria = new PhysicalEnvTO();
    	criteria.setPhysicalEnvId(physicalEnvId);

        try {
        	physicalEnvDAO = OracleMWDAOFactory.getPhysicalEnvDAO();
        	physicalEnvTO = (PhysicalEnvTO)physicalEnvDAO.findByPrimaryKey(criteria, PhysicalEnvDAO.FIND_BY_PRIMARY_KEY_SQL);
        	
        	
        	if (physicalEnvTO == null) {
        		
        		throw new WebApplicationException(Response.Status.NOT_FOUND);
        	}
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw e;
        }
        
		return physicalEnvTO;
	}
    
    @POST
	@Consumes("application/json")
//	@ValidateRequest
	public PhysicalEnvTO create(@Valid PhysicalEnvTO newPhysicalEnvTO) {
    	PhysicalEnvDAO physicalEnvDAO = null;
        Object primaryColVal = null;

        try {
        	physicalEnvDAO = OracleMWDAOFactory.getPhysicalEnvDAO();
        	primaryColVal = physicalEnvDAO.create(newPhysicalEnvTO,
        			PhysicalEnvDAO.INSERT_SQL);

        	newPhysicalEnvTO.setPhysicalEnvId(
        			((Integer) primaryColVal).intValue());
        			
        } catch (DAOException e) {
        	logger.error("Error inserting data.", e);
        	
        	throw new WebApplicationException(e);
        }
        
		return newPhysicalEnvTO;
	}
    
    @PUT
    public void update(PhysicalEnvTO physicalEnvTO) {

    	PhysicalEnvDAO physicalEnvDAO = null;
    	try {
    		physicalEnvDAO = OracleMWDAOFactory.getPhysicalEnvDAO();
    		int result = physicalEnvDAO.update(physicalEnvTO, PhysicalEnvDAO.UPDATE_SQL);
    		
    	} catch (DAOException e) {
    		logger.error("Error inserting data.", e);

    		throw new WebApplicationException(e);
    	}
    	
    	resp.setStatus(Response.Status.NO_CONTENT.getStatusCode());

    }
    
    
    @DELETE
	@Path("physicalEnv/{physicalEnvId}")
	public void delete(@PathParam("physicalEnvId") int physicalEnvId) {
		int sc = Response.Status.NO_CONTENT.getStatusCode();
		PhysicalEnvDAO physicalEnvDAO = null;
		PhysicalEnvTO physicalEnvTO = null;
		PhysicalEnvTO criteria = new PhysicalEnvTO();
		criteria.setPhysicalEnvId(physicalEnvId);
        
        try {
        	physicalEnvDAO = OracleMWDAOFactory.getPhysicalEnvDAO();
        	
        	physicalEnvTO = (PhysicalEnvTO)physicalEnvDAO.findByPrimaryKey(criteria,PhysicalEnvDAO.FIND_BY_PRIMARY_KEY_SQL);
        	
        	if (physicalEnvTO != null) {
        		physicalEnvDAO.delete(physicalEnvTO, PhysicalEnvDAO.DELETE_SQL);
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
