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
import ba.qss.m2m.mw.dao.PostavkeIzvjestajaDAO;
import ba.qss.m2m.mw.dao.PostavkeIzvjestajaTO;
import ch.qos.logback.classic.LoggerContext;

@Path("postavkeizvjestaja")
@Produces("application/json")
public class PostavkeIzvjestajaResource {


	private static final String CLASS_NAME =
			PostavkeIzvjestajaResource.class.getName();
    

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
	public PostavkeIzvjestajaTO[] select(
			@QueryParam("filterExpression") String filterExpression 
			,@Size(max=50) @DefaultValue("") @QueryParam("sort") String sort
			,@Min(value=0, message="{deviceInstance.sort.size}") @Max(100) @DefaultValue("0") @QueryParam("pageIndex") int pageIndex
			,@Min(0) @Max(1000) @DefaultValue("20") @QueryParam("pageSize") int pageSize) {
    	
    	PostavkeIzvjestajaDAO postavkeIzvjestajaDAO = null;
		List<PostavkeIzvjestajaTO> potrosaci = null;
		PostavkeIzvjestajaTO criteria = new PostavkeIzvjestajaTO();
        IntValue rowCount = new IntValue(0);
        LoggerContext loggerContext = null;

        
        
		try {
           
			postavkeIzvjestajaDAO = OracleMWDAOFactory.getPostavkeIzvjestajaDAO();
			potrosaci = (List<PostavkeIzvjestajaTO>) (List) postavkeIzvjestajaDAO.select(
                    criteria, PostavkeIzvjestajaDAO.SELECT_SQL_LIST,
                    filterExpression, sort, pageIndex, pageSize,
                    rowCount);
            
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw new WebApplicationException(e);
        }
		
		return potrosaci.toArray(new PostavkeIzvjestajaTO[potrosaci.size()]);
	}
    
    @GET
   	@Path("postavkeizvjestaja/{postavkeIzvjestajaId}")
   	public PostavkeIzvjestajaTO findDeviceInstanceByPrimaryKey(
   			
   			@PathParam("postavkeIzvjestajaId") int postavkeIzvjestajaId)
   			throws DAOException {
    	PostavkeIzvjestajaDAO postavkeIzvjestajaDAO = null;
    	PostavkeIzvjestajaTO postavkeIzvjestajaTO = null;
    	PostavkeIzvjestajaTO criteria = new PostavkeIzvjestajaTO();
       	criteria.setPostavkeIzvjestajaId(postavkeIzvjestajaId);       	

           try {
        	   postavkeIzvjestajaDAO = OracleMWDAOFactory.getPostavkeIzvjestajaDAO();
        	   postavkeIzvjestajaTO = (PostavkeIzvjestajaTO)postavkeIzvjestajaDAO.findByPrimaryKey(criteria, PostavkeIzvjestajaDAO.FIND_BY_PRIMARY_KEY_SQL);
           	
           	
           	if (postavkeIzvjestajaTO == null) {
           		
           		throw new WebApplicationException(Response.Status.NOT_FOUND);
           	}
           } catch (DAOException e) {
           	logger.error(null, e);
           	throw e;
           }
           
   		return postavkeIzvjestajaTO;
   	}
    
    @POST
	@Consumes("application/json")
//	@ValidateRequest
	public PostavkeIzvjestajaTO create(@Valid PostavkeIzvjestajaTO newPotrosacTO) {
    	PostavkeIzvjestajaDAO postavkeIzvjestajaDAO = null;
        Object primaryColVal = null;

        try {
        	postavkeIzvjestajaDAO = OracleMWDAOFactory.getPostavkeIzvjestajaDAO();
        	primaryColVal = postavkeIzvjestajaDAO.create(newPotrosacTO,
        			PostavkeIzvjestajaDAO.INSERT_SQL);

        	newPotrosacTO.setPostavkeIzvjestajaId(
        			((Integer) primaryColVal).intValue());
        			
        } catch (DAOException e) {
        	logger.error("Error inserting data.", e);
        	
        	throw new WebApplicationException(e);
        }
        
		return newPotrosacTO;
	}
    
    @PUT
    public void update(PostavkeIzvjestajaTO postavkeIzvjestajaTO) {

    	PostavkeIzvjestajaDAO postavkeIzvjestajaDAO = null;
    	try {
    		postavkeIzvjestajaDAO = OracleMWDAOFactory.getPostavkeIzvjestajaDAO();
    		int result = postavkeIzvjestajaDAO.update(postavkeIzvjestajaTO, PostavkeIzvjestajaDAO.UPDATE_SQL);
    		
    	} catch (DAOException e) {
    		logger.error("Error inserting data.", e);

    		throw new WebApplicationException(e);
    	}
    	
    	resp.setStatus(Response.Status.NO_CONTENT.getStatusCode());

    }
    
    
    @DELETE
	@Path("postavkeizvjestaja/{postavkeIzvjestajaId}")
	public void delete(@PathParam("postavkeIzvjestajaId") int postavkeIzvjestajaId) {
		int sc = Response.Status.NO_CONTENT.getStatusCode();
		PostavkeIzvjestajaDAO postavkeIzvjestajaDAO = null;
		PostavkeIzvjestajaTO postavkeIzvjestajaTO = null;
		PostavkeIzvjestajaTO criteria = new PostavkeIzvjestajaTO();
		criteria.setPostavkeIzvjestajaId(postavkeIzvjestajaId);
        
        try {
        	postavkeIzvjestajaDAO = OracleMWDAOFactory.getPostavkeIzvjestajaDAO();
        	
        	postavkeIzvjestajaTO = (PostavkeIzvjestajaTO)postavkeIzvjestajaDAO.findByPrimaryKey(criteria,PostavkeIzvjestajaDAO.FIND_BY_PRIMARY_KEY_SQL);
        	
        	if (postavkeIzvjestajaTO != null) {
        		postavkeIzvjestajaDAO.delete(postavkeIzvjestajaTO, PostavkeIzvjestajaDAO.DELETE_SQL);
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
