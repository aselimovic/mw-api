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
import ba.qss.m2m.mw.dao.PartyDAO;
import ba.qss.m2m.mw.dao.PartyRoleDAO;
import ba.qss.m2m.mw.dao.PartyRoleTO;
import ba.qss.m2m.mw.dao.PartyRoleTypeTO;
import ba.qss.m2m.mw.dao.PartyTO;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;



@Path("partyrole")
@Produces("application/json")
public class PartyRoleResource {
	
    private static final String CLASS_NAME = PartyRoleResource.class.getName();
    
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
	public PartyRoleTO[] select(
			 @DefaultValue("party_role.party_id ASC") @QueryParam("sort") String sort // XXX: Sort key expression: ORDER BY sin(i)
			,@Min(value=0, message="{partyRole.sort.size}") @Max(100) @DefaultValue("0") @QueryParam("pageIndex") int pageIndex
			,@Min(0) @Max(1000) @DefaultValue("20") @QueryParam("pageSize") int pageSize) {
		
		PartyRoleDAO partyRoleDAO = null;
		List<PartyRoleTO> parties = null;
        PartyRoleTO criteria = new PartyRoleTO();
        IntValue rowCount = new IntValue(0);
        LoggerContext loggerContext = null;

        
        loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        StatusPrinter.print(loggerContext);
        
        
        
        logger.info("Testna msg");
        
		try {
            partyRoleDAO = OracleMWDAOFactory.getPartyRoleDAO();
            parties = (List<PartyRoleTO>) (List) partyRoleDAO.select(criteria, PartyRoleDAO.SELECT_SQL_LIST, null/*filterExpression*/, sort, pageIndex, pageSize, rowCount);
            // DAOException
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw new WebApplicationException(e);
        } finally {
			//MDC.remove("MSISDN");
        }
		
		return parties.toArray(new PartyRoleTO[parties.size()]);
	}
	
	@GET
	@Path("partyrole/{partyId}/{partyRoleTypeId}")
	public PartyRoleTO findPartyRoleByPrimaryKey(@PathParam("partyId") int partyId, @PathParam("partyRoleTypeId") int partyRoleTypeId) throws DAOException {
        
		PartyRoleDAO partyRoleDAO = null;
        PartyRoleTO partyRoleTO = null;
        PartyRoleTO criteria = new PartyRoleTO();
        
        PartyTO pTO = new PartyTO();
        pTO.setPartyId(partyId);
        criteria.setPartyId(pTO);
        
        PartyRoleTypeTO prtTO = new PartyRoleTypeTO();
        prtTO.setPartyRoleTypeId(partyRoleTypeId);
        criteria.setPartyRoleTypeId(prtTO);

        try {
        	partyRoleDAO = OracleMWDAOFactory.getPartyRoleDAO();
        	partyRoleTO = (PartyRoleTO) partyRoleDAO.findByPrimaryKey(criteria, PartyRoleDAO.FIND_BY_PRIMARY_KEY_SQL);
        	// DAOException
        	
        	if (partyRoleTO == null) {throw new WebApplicationException(Response.Status.NOT_FOUND);
        	}
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw e; // Rethrow
        }
        
		return partyRoleTO;
	}
	
	@POST
	@Consumes("application/json")
//	@ValidateRequest
	public PartyRoleTO create(@Valid PartyRoleTO newPartyRoleTO) {
        
		PartyRoleDAO partyRoleDAO = null;
        try {
        	partyRoleDAO = OracleMWDAOFactory.getPartyRoleDAO();
        	partyRoleDAO.create(newPartyRoleTO, PartyRoleDAO.INSERT_SQL);
        } 
        catch (DAOException e) {
        	logger.error("Error inserting data.", e);
        	throw new WebApplicationException(e);
        }
		return newPartyRoleTO;
	}
	
	@PUT
	public void update(PartyRoleTO partyRoleTO) {
		
		PartyRoleDAO partyRoleDAO = null;    
        try {
        	partyRoleDAO = OracleMWDAOFactory.getPartyRoleDAO();
        	partyRoleDAO.update(partyRoleTO, PartyRoleDAO.UPDATE_SQL);
        } 
        catch (DAOException e) {
            logger.error("Error updating data.", e);
            throw new WebApplicationException(e);
        }
        resp.setStatus(Response.Status.NO_CONTENT.getStatusCode());
	}
	
	
	
	@DELETE
	@Path("partyrole/{partyId}/{partyRoleTypeId}")
	public void delete(@PathParam("partyId") int partyId, @PathParam("partyRoleTypeId") int partyRoleTypeId) {
		int sc = Response.Status.NO_CONTENT.getStatusCode();
        PartyRoleDAO partyRoleDAO = null;
        PartyRoleTO partyRoleTO = null;
        PartyRoleTO criteria = new PartyRoleTO();

        PartyTO pTO = new PartyTO();
        pTO.setPartyId(partyId);
        criteria.setPartyId(pTO);
        
        PartyRoleTypeTO prtTO = new PartyRoleTypeTO();
        prtTO.setPartyRoleTypeId(partyRoleTypeId);
        criteria.setPartyRoleTypeId(prtTO);
        
        try {
        	partyRoleDAO = OracleMWDAOFactory.getPartyRoleDAO();
        	partyRoleTO = (PartyRoleTO)partyRoleDAO.findByPrimaryKey(criteria, PartyRoleDAO.FIND_BY_PRIMARY_KEY_SQL);
        	// DAOException
        	
        	if (partyRoleTO != null) {
        		partyRoleDAO.delete(partyRoleTO,
        				PartyRoleDAO.DELETE_SQL);
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
