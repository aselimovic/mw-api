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
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ba.qss.framework.IntValue;
import ba.qss.framework.dataaccess.DAOException;
import ba.qss.m2m.mw.dao.OracleMWDAOFactory;
import ba.qss.m2m.mw.dao.ProfileMsisdnDAO;
import ba.qss.m2m.mw.dao.ProfileMsisdnTO;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;

@Path("profileMsisdn")
@Produces("application/json")
public class ProfileMsisdnResource {

	private static final String CLASS_NAME =
			ProfileMsisdnResource.class.getName();
    

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
    public ProfileMsisdnTO[] select(
			@QueryParam("filterExpression") String filterExpression 
			,@Size(max=50) @DefaultValue("") @QueryParam("sort") String sort
			,@Min(value=0, message="{profileMsisdn.sort.size}") @Max(100) @DefaultValue("0") @QueryParam("pageIndex") int pageIndex
			,@Min(0) @Max(1000) @DefaultValue("20") @QueryParam("pageSize") int pageSize) {
    	ProfileMsisdnDAO profileMsisdnDAO = null;
		List<ProfileMsisdnTO> profileMsisdns = null;
		ProfileMsisdnTO criteria = new ProfileMsisdnTO();
        IntValue rowCount = new IntValue(0);
        LoggerContext loggerContext = null;

        /*loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        StatusPrinter.print(loggerContext);*/

		try {           
			profileMsisdnDAO = OracleMWDAOFactory.getProfileMsisdnDAO();
			profileMsisdns = (List<ProfileMsisdnTO>) (List) profileMsisdnDAO.select(
                    criteria, ProfileMsisdnDAO.SELECT_SQL_LIST,
                    filterExpression, sort, pageIndex, pageSize,
                    rowCount);
            
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw new WebApplicationException(e);
        }
		
		return profileMsisdns.toArray(new ProfileMsisdnTO[profileMsisdns.size()]);
	}
    
    @GET
	@Path("profileMsisdn/{msisdn}")
	public ProfileMsisdnTO findProfileMsisdnByPrimaryKey(

			@PathParam("msisdn") String msisdn)
			throws DAOException {
    	ProfileMsisdnDAO profileMsisdnDAO = null;
    	ProfileMsisdnTO profileMsisdnTO = null;
    	ProfileMsisdnTO criteria = new ProfileMsisdnTO();
    	criteria.setMsisdn(msisdn);

        try {
        	profileMsisdnDAO = OracleMWDAOFactory.getProfileMsisdnDAO();
        	profileMsisdnTO = (ProfileMsisdnTO)profileMsisdnDAO.findByPrimaryKey(criteria, ProfileMsisdnDAO.FIND_BY_PRIMARY_KEY_SQL);
        	
        	
        	if (profileMsisdnTO == null) {
        		
        		throw new WebApplicationException(Response.Status.NOT_FOUND);
        	}
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw e;
        }
        
		return profileMsisdnTO;
	}
    
    @POST
    @Path("profileMsisdn")
    public ProfileMsisdnTO checkProfileMsisdn(JSONObject jsonObject){
    	
    	List<ProfileMsisdnTO> profileMsisdns = null;
    	ProfileMsisdnDAO profileMsisdnDAO = null;
    	ProfileMsisdnTO criteria = new ProfileMsisdnTO();
        IntValue rowCount = new IntValue(0);
        String filterExpression="";
    	
    	try {
    		
    		String username = jsonObject.get("username").toString();
    		String password = jsonObject.get("password").toString();
    		String msisdn = jsonObject.get("msisdn").toString();
    		
    		filterExpression="inner join profile p on profile_msisdn.profile_id=p.profile_id inner join \"USER\" u on p.user_id = u.user_id where u.user_name='"+username+"' and u.password='"+password+"' and msisdn='"+msisdn+"'";
    		//filterExpression="inner join profile p on profile_msisdn.profile_id=p.profile_id where msisdn='"+msisdn+"'";
    		
    		profileMsisdnDAO = OracleMWDAOFactory.getProfileMsisdnDAO();
    		profileMsisdns = (List<ProfileMsisdnTO>) (List) profileMsisdnDAO.select(
                    criteria, ProfileMsisdnDAO.SELECT_SQL_LIST,
                    filterExpression, "", 0, 10,
                    rowCount);
    		
        	
        	if(rowCount.getValue()>0){
        		return profileMsisdns.get(0);
        	} else {
        		throw new WebApplicationException(Response.Status.NOT_FOUND);
        	}

        	//newProfileMsisdnTO.setProfileId((Integer) primaryColVal).intValue());
        			
        } catch (DAOException e) {
        	logger.error("Error inserting data.", e);
        	
        	throw new WebApplicationException(e);
        }
    	//return newProfileMsisdnTO;
    }
    
    @POST
	@Consumes("application/json")
//	@ValidateRequest
	public ProfileMsisdnTO create(@Valid ProfileMsisdnTO newProfileMsisdnTO) {
    	ProfileMsisdnDAO profileMsisdnDAO = null;
        Object primaryColVal = null;

        try {
        	profileMsisdnDAO = OracleMWDAOFactory.getProfileMsisdnDAO();
        	primaryColVal = profileMsisdnDAO.create(newProfileMsisdnTO,
        			ProfileMsisdnDAO.INSERT_SQL);

        	//newProfileMsisdnTO.setProfileId((Integer) primaryColVal).intValue());
        			
        } catch (DAOException e) {
        	logger.error("Error inserting data.", e);
        	
        	throw new WebApplicationException(e);
        }
        
		return newProfileMsisdnTO;
	}
    
    @PUT
    public void update(ProfileMsisdnTO profileMsisdnTO) {

    	ProfileMsisdnDAO profileMsisdnDAO = null;
    	try {
    		profileMsisdnDAO = OracleMWDAOFactory.getProfileMsisdnDAO();
    		int result = profileMsisdnDAO.update(profileMsisdnTO, ProfileMsisdnDAO.UPDATE_SQL);
    		
    	} catch (DAOException e) {
    		logger.error("Error inserting data.", e);

    		throw new WebApplicationException(e);
    	}
    	
    	resp.setStatus(Response.Status.NO_CONTENT.getStatusCode());

    }
    
    @DELETE
	@Path("profileMsisdn/{msisdn}")
	public void delete(@PathParam("msisdn") String msisdn) {
		int sc = Response.Status.NO_CONTENT.getStatusCode();
		ProfileMsisdnDAO profileMsisdnDAO = null;
		ProfileMsisdnTO profileMsisdnTO = null;
		ProfileMsisdnTO criteria = new ProfileMsisdnTO();
		criteria.setMsisdn(msisdn);
        
        try {
        	profileMsisdnDAO = OracleMWDAOFactory.getProfileMsisdnDAO();
        	
        	profileMsisdnTO = (ProfileMsisdnTO)profileMsisdnDAO.findByPrimaryKey(criteria,ProfileMsisdnDAO.FIND_BY_PRIMARY_KEY_SQL);
        	
        	if (profileMsisdnTO != null) {
        		profileMsisdnDAO.delete(profileMsisdnTO, ProfileMsisdnDAO.DELETE_SQL);
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
