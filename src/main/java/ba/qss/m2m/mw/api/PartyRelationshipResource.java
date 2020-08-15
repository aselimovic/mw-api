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
import ba.qss.framework.dataaccess.TO;
import ba.qss.m2m.mw.dao.OracleMWDAOFactory;
import ba.qss.m2m.mw.dao.PartyRelationshipDAO;
import ba.qss.m2m.mw.dao.PartyRelationshipTO;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;


@Path("partyrelationship")
@Produces("application/json")
public class PartyRelationshipResource {

	

    private static final String CLASS_NAME = PartyRelationshipResource.class.getName();
    
    public static final Logger logger = LoggerFactory.getLogger(CLASS_NAME);

    @Context
    private HttpServletResponse resp;
    
    @Context
    public void setServletContext(ServletContext context) {
    	String razvoj = null;
    	razvoj = context.getInitParameter("Razvoj");
    }
    
	@GET	
//	@ValidateRequest
	public PartyRelationshipTO[] select(
			 @QueryParam("filterExpression") String filterExpression 
			,@DefaultValue("party_relationship_id ASC") @QueryParam("sort") String sort // XXX: Sort key expression: ORDER BY sin(i)
			,@Min(value=0, message="{partyRelationship.sort.size}") @Max(100) @DefaultValue("0") @QueryParam("pageIndex") int pageIndex
			,@Min(0) @Max(1000) @DefaultValue("20") @QueryParam("pageSize") int pageSize) {
		
		PartyRelationshipDAO partyRelationshipDAO = null;
		List<PartyRelationshipTO> parties = null;
		PartyRelationshipTO criteria = new PartyRelationshipTO();
        IntValue rowCount = new IntValue(0);
        LoggerContext loggerContext = null;
        loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        StatusPrinter.print(loggerContext);
        logger.info("Testna msg");
        
		try {
            partyRelationshipDAO = OracleMWDAOFactory.getPartyRelationshipDAO();
            parties = (List<PartyRelationshipTO>) (List) partyRelationshipDAO.select(criteria, PartyRelationshipDAO.SELECT_SQL_LIST, filterExpression, sort, pageIndex, pageSize, rowCount);
            // DAOException
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw new WebApplicationException(e);
        } finally {
			// MDC.remove("MSISDN");
        }
		return parties.toArray(new PartyRelationshipTO[parties.size()]);
	}
	
	@GET
	@Path("partyrelationship/{partyRelationshipId}")
	public PartyRelationshipTO findPartyRelationshipByPrimaryKey(@PathParam("partyRelationshipId") int partyRelationshipId) throws DAOException {
        PartyRelationshipDAO partyRelationshipDAO = null;
        PartyRelationshipTO partyRelationshipTO = null;
        PartyRelationshipTO criteria = new PartyRelationshipTO();
        criteria.setPartyRelationshipId(partyRelationshipId);

        try {
        	partyRelationshipDAO = OracleMWDAOFactory.getPartyRelationshipDAO();
        	partyRelationshipTO = (PartyRelationshipTO) partyRelationshipDAO.findByPrimaryKey(criteria, PartyRelationshipDAO.FIND_BY_PRIMARY_KEY_SQL);
        	
        	// DAOException
        	
        	if (partyRelationshipTO == null) {throw new WebApplicationException(Response.Status.NOT_FOUND);
        	}
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw e; // Rethrow
        }
        
		return partyRelationshipTO;
	}
	
	@POST
	@Consumes("application/json")
//	@ValidateRequest
	public PartyRelationshipTO create(@Valid PartyRelationshipTO newPartyRelationshipTO) {
        PartyRelationshipDAO partyRelationshipDAO = null;
        Object primaryColVal = null;

        try {
        	partyRelationshipDAO = OracleMWDAOFactory.getPartyRelationshipDAO();
        	primaryColVal = partyRelationshipDAO.create(newPartyRelationshipTO, PartyRelationshipDAO.INSERT_SQL);
        	newPartyRelationshipTO.setPartyRelationshipId(((Integer) primaryColVal).intValue());
        } 
        catch (DAOException e) {
        	logger.error("Error inserting data.", e);
        	// Construct a new instance with a blank message and default HTTP
        	// status code of 500
        	throw new WebApplicationException(e);
        }
        
		return newPartyRelationshipTO;
	}
	
	@PUT
	public void update(PartyRelationshipTO partyRelationshipTO) {
		
		PartyRelationshipDAO partyRelationshipDAO = null;    
        try {
        	partyRelationshipDAO = OracleMWDAOFactory.getPartyRelationshipDAO();
        	partyRelationshipDAO.update(partyRelationshipTO, PartyRelationshipDAO.UPDATE_SQL);
        } 
        catch (DAOException e) {
            logger.error("Error updating data.", e);
            throw new WebApplicationException(e);
        }
        resp.setStatus(Response.Status.NO_CONTENT.getStatusCode());
	}
	
	@DELETE
	@Path("partyrelationship/{partyRelationshipId}")
	public void delete(@PathParam("partyRelationshipId") int partyRelationshipId) {
		int sc = Response.Status.NO_CONTENT.getStatusCode();
        PartyRelationshipDAO partyRelationshipDAO = null;
        PartyRelationshipTO partyRelationshipTO = null;
        PartyRelationshipTO criteria = new PartyRelationshipTO();
        criteria.setPartyRelationshipId(partyRelationshipId);
        
        try {
        	partyRelationshipDAO = OracleMWDAOFactory.getPartyRelationshipDAO();
        	partyRelationshipTO = (PartyRelationshipTO)partyRelationshipDAO.findByPrimaryKey(criteria, PartyRelationshipDAO.FIND_BY_PRIMARY_KEY_SQL);
        	// DAOException
        	
        	if (partyRelationshipTO != null) {
        		partyRelationshipDAO.delete(partyRelationshipTO, PartyRelationshipDAO.DELETE_SQL);
        		// OptimisticLockException, DAOException
        	} else {
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
