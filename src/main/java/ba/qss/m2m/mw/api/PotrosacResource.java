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
import ba.qss.m2m.mw.dao.PotrosacDAO;
import ba.qss.m2m.mw.dao.PotrosacTO;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;

@Path("potrosac")
@Produces("application/json")
public class PotrosacResource {
	
	private static final String CLASS_NAME =
			DeviceInstanceResource.class.getName();
    

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
	public PotrosacTO[] select(
			@QueryParam("filterExpression") String filterExpression 
			,@Size(max=50) @DefaultValue("") @QueryParam("sort") String sort
			,@Min(value=0, message="{deviceInstance.sort.size}") @Max(100) @DefaultValue("0") @QueryParam("pageIndex") int pageIndex
			,@Min(0) @Max(1000) @DefaultValue("20") @QueryParam("pageSize") int pageSize) {
    	
    	PotrosacDAO potrosacDAO = null;
		List<PotrosacTO> potrosaci = null;
		PotrosacTO criteria = new PotrosacTO();
        IntValue rowCount = new IntValue(0);
        LoggerContext loggerContext = null;

        
        
		try {
           
			potrosacDAO = OracleMWDAOFactory.getPotrosacDAO();
			potrosaci = (List<PotrosacTO>) (List) potrosacDAO.select(
                    criteria, PotrosacDAO.SELECT_SQL_LIST,
                    filterExpression, sort, pageIndex, pageSize,
                    rowCount);
            
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw new WebApplicationException(e);
        }
		
		return potrosaci.toArray(new PotrosacTO[potrosaci.size()]);
	}
    
    @GET
    @Path("{pageId}/{partyId}")
	public PotrosacTO[] selectByPage(
			@PathParam("pageId") int pageId,
			@PathParam("partyId") int partyId,
			@QueryParam("filterExpression") String filterExpression,
			@Size(max=50) @DefaultValue("") @QueryParam("sort") String sort) {
    	
    	PotrosacDAO potrosacDAO = null;
		List<PotrosacTO> potrosaci = null;
		PotrosacTO criteria = new PotrosacTO();
        IntValue rowCount = new IntValue(0);
        LoggerContext loggerContext = null;

        String fExp = " INNER JOIN party_relationship ON potrosac.party_id = party_relationship.party_id_to WHERE party_relationship.party_id_from="+partyId;
        if(filterExpression!=null){
        	fExp+=" AND potrosac.sifra_potrosaca like '%"+filterExpression+"%'";
        }
        
		try {
			potrosacDAO = OracleMWDAOFactory.getPotrosacDAO();
			potrosaci = (List<PotrosacTO>) (List) potrosacDAO.select(
                    criteria, PotrosacDAO.SELECT_SQL_LIST,
                    fExp, sort, pageId, 20,
                    rowCount);
            
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw new WebApplicationException(e);
        }
		
		return potrosaci.toArray(new PotrosacTO[potrosaci.size()]);
	}
    
    @GET
    @Path("/partner/{partyIds}/{partnerId}")
	public PotrosacTO[] selectForPartner(
			@PathParam("partyIds") String partyIds,
			@PathParam("partnerId") int partnerId,
			@Size(max=50) @DefaultValue("") @QueryParam("sort") String sort) {
    	
    	PotrosacDAO potrosacDAO = null;
		List<PotrosacTO> potrosaci = null;
		PotrosacTO criteria = new PotrosacTO();
        IntValue rowCount = new IntValue(0);
        LoggerContext loggerContext = null;

        String fExp = " INNER JOIN party_relationship ON potrosac.party_id = party_relationship.party_id_to WHERE party_relationship.party_id_from="+partnerId;
        fExp+=" AND potrosac.party_id in ("+partyIds+")";

		try {
			potrosacDAO = OracleMWDAOFactory.getPotrosacDAO();
			potrosaci = (List<PotrosacTO>) (List) potrosacDAO.select(
                    criteria, PotrosacDAO.SELECT_SQL_LIST,
                    fExp, sort, 0, 20,
                    rowCount);
            
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw new WebApplicationException(e);
        }
		
		return potrosaci.toArray(new PotrosacTO[potrosaci.size()]);
	}
    
    @GET
   	@Path("potrosac/{potrosacId}")
   	public PotrosacTO findDeviceInstanceByPrimaryKey(
   			
   			@PathParam("potrosacId") int potrosacId)
   			throws DAOException {
    	PotrosacDAO potrosacDAO = null;
    	PotrosacTO potrosacTO = null;
    	PotrosacTO criteria = new PotrosacTO();
       	criteria.setPotrosacId(potrosacId);       	

           try {
        	   potrosacDAO = OracleMWDAOFactory.getPotrosacDAO();
        	   potrosacTO = (PotrosacTO)potrosacDAO.findByPrimaryKey(criteria, PotrosacDAO.FIND_BY_PRIMARY_KEY_SQL);
           	
           	
           	if (potrosacTO == null) {
           		
           		throw new WebApplicationException(Response.Status.NOT_FOUND);
           	}
           } catch (DAOException e) {
           	logger.error(null, e);
           	throw e;
           }
           
   		return potrosacTO;
   	}
    
    @POST
	@Consumes("application/json")
//	@ValidateRequest
	public PotrosacTO create(@Valid PotrosacTO newPotrosacTO) {
    	PotrosacDAO potrosacDAO = null;
        Object primaryColVal = null;

        try {
        	potrosacDAO = OracleMWDAOFactory.getPotrosacDAO();
        	primaryColVal = potrosacDAO.create(newPotrosacTO,
        			PotrosacDAO.INSERT_SQL);

        	newPotrosacTO.setPotrosacId(
        			((Integer) primaryColVal).intValue());
        			
        } catch (DAOException e) {
        	logger.error("Error inserting data.", e);
        	
        	throw new WebApplicationException(e);
        }
        
		return newPotrosacTO;
	}
    
    @PUT
    public void update(PotrosacTO potrosacTO) {

    	PotrosacDAO potrosacDAO = null;
    	try {
    		potrosacDAO = OracleMWDAOFactory.getPotrosacDAO();
    		int result = potrosacDAO.update(potrosacTO, PotrosacDAO.UPDATE_SQL);
    		
    	} catch (DAOException e) {
    		logger.error("Error inserting data.", e);

    		throw new WebApplicationException(e);
    	}
    	
    	resp.setStatus(Response.Status.NO_CONTENT.getStatusCode());

    }
    
    
    @DELETE
	@Path("potrosac/{potrosacId}")
	public void delete(@PathParam("potrosacId") int potrosacId) {
		int sc = Response.Status.NO_CONTENT.getStatusCode();
		PotrosacDAO potrosacDAO = null;
		PotrosacTO potrosacTO = null;
		PotrosacTO criteria = new PotrosacTO();
		criteria.setPotrosacId(potrosacId);
        
        try {
        	potrosacDAO = OracleMWDAOFactory.getPotrosacDAO();
        	
        	potrosacTO = (PotrosacTO)potrosacDAO.findByPrimaryKey(criteria,PotrosacDAO.FIND_BY_PRIMARY_KEY_SQL);
        	
        	if (potrosacTO != null) {
        		potrosacDAO.delete(potrosacTO, PotrosacDAO.DELETE_SQL);
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
