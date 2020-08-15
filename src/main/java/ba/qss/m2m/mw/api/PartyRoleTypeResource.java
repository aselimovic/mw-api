package ba.qss.m2m.mw.api;

import java.util.List;

import javax.persistence.OptimisticLockException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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
import ba.qss.m2m.mw.dao.OracleMWDAOFactory;
import ba.qss.m2m.mw.dao.PartyRoleTypeDAO;
import ba.qss.m2m.mw.dao.PartyRoleTypeTO;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;

@Path("partyroletype")
@Produces("application/json")
public class PartyRoleTypeResource {

	   private static final String CLASS_NAME = PartyRoleTypeResource.class.getName();
	    
	    public static final Logger logger = LoggerFactory.getLogger(CLASS_NAME);

	    @Context
	    private HttpServletResponse resp;
	    
	    @Context
	    public void setServletContext(ServletContext context) {
	    	String razvoj = null;
	    	razvoj = context.getInitParameter("Razvoj");
	    }
	    
		@GET
//		@ValidateRequest
		public PartyRoleTypeTO[] select(
				@QueryParam("filterExpression") String filterExpression
				,@DefaultValue("party_role_type_id ASC") @QueryParam("sort") String sort // XXX: Sort key expression: ORDER BY sin(i)
				,@Min(value=0, message="{partyRoleType.sort.size}") @Max(100) @DefaultValue("0") @QueryParam("pageIndex") int pageIndex
				,@Min(0) @Max(1000) @DefaultValue("20") @QueryParam("pageSize") int pageSize) {
			PartyRoleTypeDAO partyRoleTypeDAO = null;
			List<PartyRoleTypeTO> partyRoleTypes = null;
	        PartyRoleTypeTO criteria = new PartyRoleTypeTO();
	        IntValue rowCount = new IntValue(0);
	        LoggerContext loggerContext = null;

	        loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
	        StatusPrinter.print(loggerContext);
	        
	        MDC.put("MSISDN", "38761712805");
	        
	        logger.info("Testna msg");
	        
			try {
	            partyRoleTypeDAO = OracleMWDAOFactory.getPartyRoleTypeDAO();
	            partyRoleTypes = (List<PartyRoleTypeTO>) (List) partyRoleTypeDAO.select(criteria, PartyRoleTypeDAO.SELECT_SQL_LIST, filterExpression, sort, pageIndex, pageSize, rowCount);
	         } 
			catch (DAOException e) {
	        	logger.error(null, e);
	        	throw new WebApplicationException(e);
	        } finally {
				MDC.remove("MSISDN");
	        }
			
			return partyRoleTypes.toArray(new PartyRoleTypeTO[partyRoleTypes.size()]);
		}
		
		@GET
		@Path("partyroletype/{partyRoleTypeId}")
		public PartyRoleTypeTO findPartyRoleTypeByPrimaryKey(@PathParam("partyRoleTypeId") int partyRoleTypeId) throws DAOException {
	        PartyRoleTypeDAO partyRoleTypeDAO = null;
	        PartyRoleTypeTO partyRoleTypeTO = null;
	        PartyRoleTypeTO criteria = new PartyRoleTypeTO();
	        criteria.setPartyRoleTypeId(partyRoleTypeId);

	        try {
	        	partyRoleTypeDAO = OracleMWDAOFactory.getPartyRoleTypeDAO();
	        	partyRoleTypeTO = (PartyRoleTypeTO) partyRoleTypeDAO.findByPrimaryKey(criteria, PartyRoleTypeDAO.FIND_BY_PRIMARY_KEY_SQL);
	        	// DAOException
	        	
	        	if (partyRoleTypeTO == null) {throw new WebApplicationException(Response.Status.NOT_FOUND);
	        	}
	        } catch (DAOException e) {
	        	logger.error(null, e);
	        	throw e; // Rethrow
	        }
	        
			return partyRoleTypeTO;
		}
		
		@POST
		@Consumes("application/json")
//		@ValidateRequest
		public PartyRoleTypeTO create(@Valid PartyRoleTypeTO newPartyRoleTypeTO) {
	        PartyRoleTypeDAO partyRoleTypeDAO = null;
	        Object primaryColVal = null;

	        try {
	        	partyRoleTypeDAO = OracleMWDAOFactory.getPartyRoleTypeDAO();
	        	primaryColVal = partyRoleTypeDAO.create(newPartyRoleTypeTO, PartyRoleTypeDAO.INSERT_SQL);
	        	newPartyRoleTypeTO.setPartyRoleTypeId(((Integer) primaryColVal).intValue());
	        } 
	        catch (DAOException e) {
	        	logger.error("Error inserting data.", e);
	        	// Construct a new instance with a blank message and default HTTP
	        	// status code of 500
	        	throw new WebApplicationException(e);
	        }
	        
			return newPartyRoleTypeTO;
		}
		
		@PUT
		public void update(PartyRoleTypeTO partyRoleTypeTO) {
			
			PartyRoleTypeDAO partyRoleTypeDAO = null;    
	        try {
	        	partyRoleTypeDAO = OracleMWDAOFactory.getPartyRoleTypeDAO();
	        	partyRoleTypeDAO.update(partyRoleTypeTO, PartyRoleTypeDAO.UPDATE_SQL);
	        } 
	        catch (DAOException e) {
	        	resp.setStatus(Response.Status.NO_CONTENT.getStatusCode());
	            logger.error("Error updating data.", e);
	            throw new WebApplicationException(e);
	        }
	        resp.setStatus(Response.Status.NO_CONTENT.getStatusCode());
		}
		
		@DELETE
		@Path("partyroletype/{partyRoleTypeId}")
		public void delete(@PathParam("partyRoleTypeId") int partyRoleTypeId) {
			int sc = Response.Status.NO_CONTENT.getStatusCode();
	        PartyRoleTypeDAO partyRoleTypeDAO = null;
	        PartyRoleTypeTO partyRoleTypeTO = null;
	        PartyRoleTypeTO criteria = new PartyRoleTypeTO();
	        criteria.setPartyRoleTypeId(partyRoleTypeId);
	        
	        try {
	        	partyRoleTypeDAO = OracleMWDAOFactory.getPartyRoleTypeDAO();
	        	partyRoleTypeTO = (PartyRoleTypeTO)partyRoleTypeDAO.findByPrimaryKey(criteria, PartyRoleTypeDAO.FIND_BY_PRIMARY_KEY_SQL);
	        	// DAOException
	        	
	        	if (partyRoleTypeTO != null) {
	        		partyRoleTypeDAO.delete(partyRoleTypeTO, PartyRoleTypeDAO.DELETE_SQL);
	        	} 
	        	else {
	        		// We are telling the client that the thing we want to delete is
	        		// already gone (410).
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
