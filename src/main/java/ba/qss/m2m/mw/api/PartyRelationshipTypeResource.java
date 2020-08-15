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


import ba.qss.m2m.mw.dao.PartyRelationshipTypeDAO;
import ba.qss.m2m.mw.dao.PartyRelationshipTypeTO;


@Path("partyrelationshiptype")
@Produces("application/json")
public class PartyRelationshipTypeResource {

	

    private static final String CLASS_NAME = PartyRelationshipTypeResource.class.getName();
    
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
	public PartyRelationshipTypeTO[] select(
			@DefaultValue("party_relationship_type_id ASC") @QueryParam("sort") String sort // XXX: Sort key expression: ORDER BY sin(i)
			,@Min(value=0, message="{partyRelationshipType.sort.size}") @Max(100) @DefaultValue("0") @QueryParam("pageIndex") int pageIndex
			,@Min(0) @Max(1000) @DefaultValue("20") @QueryParam("pageSize") int pageSize) {
		
		PartyRelationshipTypeDAO partyRelationshipTypeDAO = null;
		List<PartyRelationshipTypeTO> parties = null;
		PartyRelationshipTypeTO criteria = new PartyRelationshipTypeTO();
        IntValue rowCount = new IntValue(0);
        LoggerContext loggerContext = null;
        loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        StatusPrinter.print(loggerContext);
        logger.info("Testna msg");
        
		try {
            partyRelationshipTypeDAO = OracleMWDAOFactory.getPartyRelationshipTypeDAO();
            parties = (List<PartyRelationshipTypeTO>) (List) partyRelationshipTypeDAO.select(criteria, PartyRelationshipTypeDAO.SELECT_SQL_LIST, /*filterExpression,*/ sort, pageIndex, pageSize, rowCount);
            // DAOException
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw new WebApplicationException(e);
        } finally {
			// MDC.remove("MSISDN");
        }
		return parties.toArray(new PartyRelationshipTypeTO[parties.size()]);
	}
	
	@GET
	@Path("partyrelationshiptype/{partyRelationshipTypeId}")
	public PartyRelationshipTypeTO findPartyRelationshipTypeByPrimaryKey(@PathParam("partyRelationshipTypeId") int partyRelationshipTypeId) throws DAOException {
        PartyRelationshipTypeDAO partyRelationshipTypeDAO = null;
        PartyRelationshipTypeTO partyRelationshipTypeTO = null;
        PartyRelationshipTypeTO criteria = new PartyRelationshipTypeTO();
        criteria.setPartyRelationshipTypeId(partyRelationshipTypeId);

        try {
        	partyRelationshipTypeDAO = OracleMWDAOFactory.getPartyRelationshipTypeDAO();
        	partyRelationshipTypeTO = (PartyRelationshipTypeTO) partyRelationshipTypeDAO.findByPrimaryKey(criteria, PartyRelationshipTypeDAO.FIND_BY_PRIMARY_KEY_SQL);
        	
        	// DAOException
        	
        	if (partyRelationshipTypeTO == null) {throw new WebApplicationException(Response.Status.NOT_FOUND);
        	}
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw e; // Rethrow
        }
        
		return partyRelationshipTypeTO;
	}
	
	@POST
	@Consumes("application/json")
//	@ValidateRequest
	public PartyRelationshipTypeTO create(@Valid PartyRelationshipTypeTO newPartyRelationshipTypeTO) {
        PartyRelationshipTypeDAO partyRelationshipTypeDAO = null;
        Object primaryColVal = null;

        try {
        	partyRelationshipTypeDAO = OracleMWDAOFactory.getPartyRelationshipTypeDAO();
        	primaryColVal = partyRelationshipTypeDAO.create(newPartyRelationshipTypeTO, PartyRelationshipTypeDAO.INSERT_SQL);
        	newPartyRelationshipTypeTO.setPartyRelationshipTypeId(((Integer) primaryColVal).intValue());
        } 
        catch (DAOException e) {
        	logger.error("Error inserting data.", e);
        	// Construct a new instance with a blank message and default HTTP
        	// status code of 500
        	throw new WebApplicationException(e);
        }
        
		return newPartyRelationshipTypeTO;
	}
	
	@PUT
	public void update(PartyRelationshipTypeTO partyRelationshipTypeTO) {
		
		PartyRelationshipTypeDAO partyRelationshipTypeDAO = null;    
        try {
        	partyRelationshipTypeDAO = OracleMWDAOFactory.getPartyRelationshipTypeDAO();
        	partyRelationshipTypeDAO.update(partyRelationshipTypeTO, PartyRelationshipTypeDAO.UPDATE_SQL);
        } 
        catch (DAOException e) {
            logger.error("Error updating data.", e);
            throw new WebApplicationException(e);
        }
        resp.setStatus(Response.Status.NO_CONTENT.getStatusCode());
	}
	
	@DELETE
	@Path("partyrelationshiptype/{partyRelationshipTypeId}")
	public void delete(@PathParam("partyRelationshipTypeId") int partyRelationshipTypeId) {
		
		int sc = Response.Status.NO_CONTENT.getStatusCode();
        PartyRelationshipTypeDAO partyRelationshipTypeDAO = null;
        PartyRelationshipTypeTO partyRelationshipTypeTO = null;
        PartyRelationshipTypeTO criteria = new PartyRelationshipTypeTO();
        criteria.setPartyRelationshipTypeId(partyRelationshipTypeId);
        
        try {
        	partyRelationshipTypeDAO = OracleMWDAOFactory.getPartyRelationshipTypeDAO();
        	partyRelationshipTypeTO = (PartyRelationshipTypeTO)partyRelationshipTypeDAO.findByPrimaryKey(criteria, PartyRelationshipTypeDAO.FIND_BY_PRIMARY_KEY_SQL);
        	// DAOException
        	
        	if (partyRelationshipTypeTO != null) {
        		partyRelationshipTypeDAO.delete(partyRelationshipTypeTO, PartyRelationshipTypeDAO.DELETE_SQL);
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
