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
import ba.qss.m2m.mw.dao.PartyRoleTypeTO;
import ba.qss.m2m.mw.dao.PartyTO;
import ba.qss.m2m.mw.dao.PartyUserAssociationDAO;
import ba.qss.m2m.mw.dao.PartyUserAssociationTO;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;

@Path("partyUserAssociation")
@Produces("application/json")
public class PartyUserAssociationResource {
	
private static final String CLASS_NAME = PartyUserAssociationResource.class.getName();
    
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
	public PartyUserAssociationTO[] select(
			 @QueryParam("filterExpression") String filterExpression 
			 ,@DefaultValue("party_user_association.party_id ASC") @QueryParam("sort") String sort // XXX: Sort key expression: ORDER BY sin(i)
			,@Min(value=0, message="{partyUserAssociation.sort.size}") @Max(100) @DefaultValue("0") @QueryParam("pageIndex") int pageIndex
			,@Min(0) @Max(1000) @DefaultValue("20") @QueryParam("pageSize") int pageSize) {
		
		PartyUserAssociationDAO partyUserAssociationDAO = null;
		List<PartyUserAssociationTO> partyUserAssociations = null;
		PartyUserAssociationTO criteria = new PartyUserAssociationTO();
        IntValue rowCount = new IntValue(0);
        LoggerContext loggerContext = null;

        
        loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        StatusPrinter.print(loggerContext);
        
        
		try {
			partyUserAssociationDAO = OracleMWDAOFactory.getPartyUserAssociationDAO();
			partyUserAssociations = (List<PartyUserAssociationTO>) (List) partyUserAssociationDAO.select(criteria, PartyUserAssociationDAO.SELECT_SQL_LIST, filterExpression, sort, pageIndex, pageSize, rowCount);
            // DAOException
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw new WebApplicationException(e);
        } finally {
			//MDC.remove("MSISDN");
        }
		
		return partyUserAssociations.toArray(new PartyUserAssociationTO[partyUserAssociations.size()]);
	}
	
	@GET
	@Path("partyUserAssociation/{partyId}/{partyRoleTypeId}")
	public PartyUserAssociationTO findDeviceByPrimaryKey(
			
			@PathParam("partyId") int partyId, @PathParam("partyRoleTypeId") int partyRoleTypeId)
			throws DAOException {
		PartyUserAssociationDAO partyUserAssociationDAO = null;
    	PartyUserAssociationTO partyUserAssociationTO = null;
    	PartyUserAssociationTO criteria = new PartyUserAssociationTO();
    	PartyTO partyTO = new PartyTO();
    	partyTO.setPartyId(partyId);
    	criteria.setPartyId(partyTO);
    	PartyRoleTypeTO partyRoleTypeTO = new PartyRoleTypeTO();
    	partyRoleTypeTO.setPartyRoleTypeId(partyRoleTypeId);
    	criteria.setPartyRoleTypeId(partyRoleTypeTO);

        try {
        	partyUserAssociationDAO = OracleMWDAOFactory.getPartyUserAssociationDAO();
        	partyUserAssociationTO = (PartyUserAssociationTO)partyUserAssociationDAO.findByPrimaryKey(criteria, PartyUserAssociationDAO.FIND_BY_PRIMARY_KEY_SQL);
        	
        	
        	if (partyUserAssociationTO == null) {
        		
        		throw new WebApplicationException(Response.Status.NOT_FOUND);
        	}
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw e;
        }
        
		return partyUserAssociationTO;
	}
	
	@POST
	@Consumes("application/json")
//	@ValidateRequest
	public PartyUserAssociationTO create(@Valid PartyUserAssociationTO newPartyUserAssociationTO) {
		PartyUserAssociationDAO partyUserAssociationDAO = null;
        Object primaryColVal = null;

        try {
        	partyUserAssociationDAO = OracleMWDAOFactory.getPartyUserAssociationDAO();
        	primaryColVal = partyUserAssociationDAO.create(newPartyUserAssociationTO,
        			PartyUserAssociationDAO.INSERT_SQL);

        			
        } catch (DAOException e) {
        	logger.error("Error inserting data.", e);
        	
        	throw new WebApplicationException(e);
        }
        
		return newPartyUserAssociationTO;
	}
	
	@DELETE
	@Path("partyUserAssociation/{partyId}/{partyRoleTypeId}")
	public void delete(@PathParam("partyId") int partyId, @PathParam("partyRoleTypeId") int partyRoleTypeId) {
		int sc = Response.Status.NO_CONTENT.getStatusCode();
		PartyUserAssociationDAO partyUserAssociationDAO = null;
		PartyUserAssociationTO partyUserAssociationTO = null;
		PartyUserAssociationTO criteria = new PartyUserAssociationTO();
		PartyTO partyTO = new PartyTO();
    	partyTO.setPartyId(partyId);
    	criteria.setPartyId(partyTO);
    	PartyRoleTypeTO partyRoleTypeTO = new PartyRoleTypeTO();
    	partyRoleTypeTO.setPartyRoleTypeId(partyRoleTypeId);
    	criteria.setPartyRoleTypeId(partyRoleTypeTO);
        
        try {
        	partyUserAssociationDAO = OracleMWDAOFactory.getPartyUserAssociationDAO();
        	
        	partyUserAssociationTO = (PartyUserAssociationTO)partyUserAssociationDAO.findByPrimaryKey(criteria,PartyUserAssociationDAO.FIND_BY_PRIMARY_KEY_SQL);
        	
        	if (partyUserAssociationTO != null) {
        		partyUserAssociationDAO.delete(partyUserAssociationTO, PartyUserAssociationDAO.DELETE_SQL);
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
