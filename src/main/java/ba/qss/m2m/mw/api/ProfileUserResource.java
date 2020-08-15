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
import ba.qss.framework.dataaccess.UserTO;
import ba.qss.m2m.mw.dao.OracleMWDAOFactory;
import ba.qss.m2m.mw.dao.ProfileUserDAO;
import ba.qss.m2m.mw.dao.ProfileUserTO;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;

@Path("profileUser")
@Produces("application/json")
public class ProfileUserResource {

	private static final String CLASS_NAME =
			ProfileUserResource.class.getName();
    

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
    public ProfileUserTO[] select(
			@QueryParam("filterExpression") String filterExpression 
			,@Size(max=50) @DefaultValue("") @QueryParam("sort") String sort
			,@Min(value=0, message="{profileUser.sort.size}") @Max(100) @DefaultValue("0") @QueryParam("pageIndex") int pageIndex
			,@Min(0) @Max(1000) @DefaultValue("20") @QueryParam("pageSize") int pageSize) {
    	ProfileUserDAO profileUserDAO = null;
		List<ProfileUserTO> profileUsers = null;
		ProfileUserTO criteria = new ProfileUserTO();
        IntValue rowCount = new IntValue(0);

		try {           
			profileUserDAO = OracleMWDAOFactory.getProfileUserDAO();
			profileUsers = (List<ProfileUserTO>) (List) profileUserDAO.select(
                    criteria, ProfileUserDAO.SELECT_SQL_LIST,
                    filterExpression, sort, pageIndex, pageSize,
                    rowCount);
            
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw new WebApplicationException(e);
        }
		
		return profileUsers.toArray(new ProfileUserTO[profileUsers.size()]);
	}
    
    @GET
    @Path("profileUsername/{username}")
    public ProfileUserTO[] selectByUsername(@PathParam("username") String username) {
    	ProfileUserDAO profileUserDAO = null;
		List<ProfileUserTO> profileUsers = null;
		ProfileUserTO criteria = new ProfileUserTO();
        IntValue rowCount = new IntValue(0);

		try {       
			String filterExpression="where u.user_name='"+username+"'";
			profileUserDAO = OracleMWDAOFactory.getProfileUserDAO();
			profileUsers = (List<ProfileUserTO>) (List) profileUserDAO.select(
                    criteria, ProfileUserDAO.SELECT_SQL_LIST,
                    filterExpression, "", 0, 20,
                    rowCount);
            
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw new WebApplicationException(e);
        }
		
		return profileUsers.toArray(new ProfileUserTO[profileUsers.size()]);
	}
    
    @GET
	@Path("profileUser/{profileId}/{userId}")
	public ProfileUserTO findProfileUserByPrimaryKey(
			@PathParam("userId") int userId,
			@PathParam("profileId") int profileId)
			throws DAOException {
    	ProfileUserDAO profileUserDAO = null;
    	ProfileUserTO profileUserTO = null;
    	ProfileUserTO criteria = new ProfileUserTO();
    	criteria.setProfileId(profileId);
    	criteria.setUserId(userId);

        try {
        	profileUserDAO = OracleMWDAOFactory.getProfileUserDAO();
        	profileUserTO = (ProfileUserTO)profileUserDAO.findByPrimaryKey(criteria, ProfileUserDAO.FIND_BY_PRIMARY_KEY_SQL);
        	
        	
        	if (profileUserTO == null) {
        		
        		throw new WebApplicationException(Response.Status.NOT_FOUND);
        	}
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw e;
        }
        
		return profileUserTO;
	}
    
    @POST
	@Consumes("application/json")
//	@ValidateRequest
	public ProfileUserTO create(@Valid ProfileUserTO newProfileUserTO) {
    	ProfileUserDAO profileUserDAO = null;
        Object primaryColVal = null;

        try {
        	profileUserDAO = OracleMWDAOFactory.getProfileUserDAO();
        	primaryColVal = profileUserDAO.create(newProfileUserTO,
        			ProfileUserDAO.INSERT_SQL);

        	//newProfileUserTO.setProfileId((Integer) primaryColVal).intValue());
        			
        } catch (DAOException e) {
        	logger.error("Error inserting data.", e);
        	
        	throw new WebApplicationException(e);
        }
        
		return newProfileUserTO;
	}
    
    @PUT
    public void update(ProfileUserTO profileUserTO) {

    	ProfileUserDAO profileUserDAO = null;
    	try {
    		profileUserDAO = OracleMWDAOFactory.getProfileUserDAO();
    		int result = profileUserDAO.update(profileUserTO, ProfileUserDAO.UPDATE_SQL);
    		
    	} catch (DAOException e) {
    		logger.error("Error inserting data.", e);

    		throw new WebApplicationException(e);
    	}
    	
    	resp.setStatus(Response.Status.NO_CONTENT.getStatusCode());

    }
    
    @DELETE
	@Path("profileUser/{profileId}/{userId}")
	public void delete(@PathParam("userId") int userId,
			@PathParam("profileId") int profileId) {
		int sc = Response.Status.NO_CONTENT.getStatusCode();
		ProfileUserDAO profileUserDAO = null;
		ProfileUserTO profileUserTO = null;
		ProfileUserTO criteria = new ProfileUserTO();
		criteria.setProfileId(profileId);
    	criteria.setUserId(userId);
    	UserTO userTO = new UserTO();
        
        try {
        	profileUserDAO = OracleMWDAOFactory.getProfileUserDAO();
        	
        	profileUserTO = (ProfileUserTO)profileUserDAO.findByPrimaryKey(criteria,ProfileUserDAO.FIND_BY_PRIMARY_KEY_SQL);
        	
        	if (profileUserTO != null) {
        		profileUserDAO.delete(profileUserTO, ProfileUserDAO.DELETE_SQL);
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
    
    
    @POST
	@Consumes("application/json")
    @Path("user/")
	public ProfileUserTO createUserProfileUser(JSONObject newUserJson) {
    	ProfileUserDAO profileUserDAO = null;
        int primaryColVal = 0;
        ProfileUserTO newProfileUserTO=new ProfileUserTO();

        try {
        	profileUserDAO = OracleMWDAOFactory.getProfileUserDAO();
        	primaryColVal = profileUserDAO.createUser(newUserJson.get("username").toString(), newUserJson.get("password").toString());

        	newProfileUserTO.setProfileId((Integer) primaryColVal);
        			
        } catch (DAOException e) {
        	logger.error("Error inserting data.", e);
        	
        	throw new WebApplicationException(e);
        }
        
		return newProfileUserTO;
	}
    
}
