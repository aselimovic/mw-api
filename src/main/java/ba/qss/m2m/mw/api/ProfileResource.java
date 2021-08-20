package ba.qss.m2m.mw.api;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
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
import ba.qss.m2m.mw.dao.ProfileDAO;
import ba.qss.m2m.mw.dao.ProfileMsisdnDAO;
import ba.qss.m2m.mw.dao.ProfileMsisdnTO;
import ba.qss.m2m.mw.dao.ProfileTO;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;

@Path("profile")
@Produces("application/json")
public class ProfileResource {

	private static final String CLASS_NAME =
			ProfileResource.class.getName();
    

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
    public ProfileTO[] select(
			@QueryParam("filterExpression") String filterExpression 
			,@Size(max=50) @DefaultValue("") @QueryParam("sort") String sort
			,@Min(value=0, message="{profile.sort.size}") @Max(100) @DefaultValue("0") @QueryParam("pageIndex") int pageIndex
			,@Min(0) @Max(1000) @DefaultValue("20") @QueryParam("pageSize") int pageSize) {
    	ProfileDAO profileDAO = null;
		List<ProfileTO> profiles = null;
		ProfileTO criteria = new ProfileTO();
        IntValue rowCount = new IntValue(0);
        LoggerContext loggerContext = null;

        /*loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        StatusPrinter.print(loggerContext);*/

		try {           
			profileDAO = OracleMWDAOFactory.getProfileDAO();
			profiles = (List<ProfileTO>) (List) profileDAO.select(
                    criteria, ProfileDAO.SELECT_SQL_LIST,
                    filterExpression, sort, pageIndex, pageSize,
                    rowCount);
            
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw new WebApplicationException(e);
        }
		
		return profiles.toArray(new ProfileTO[profiles.size()]);
	}
    
    @GET
	@Path("profile/{profileId}")
	public ProfileTO findProfileByPrimaryKey(
			
			@PathParam("profileId") int profileId)
			throws DAOException {
    	ProfileDAO profileDAO = null;
    	ProfileTO profileTO = null;
    	ProfileTO criteria = new ProfileTO();
    	criteria.setProfileId(profileId);

        try {
        	profileDAO = OracleMWDAOFactory.getProfileDAO();
        	profileTO = (ProfileTO)profileDAO.findByPrimaryKey(criteria, ProfileDAO.FIND_BY_PRIMARY_KEY_SQL);
        	
        	
        	if (profileTO == null) {
        		
        		throw new WebApplicationException(Response.Status.NOT_FOUND);
        	}
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw e;
        }
        
		return profileTO;
	}

    @GET
    @Path("profile/{password}/{profileId}")
    public void updatePass(
    		@PathParam("password") String password,
    		@PathParam("profileId") int profileId) {

    	ProfileDAO profileDAO = null;
    	try {
    		profileDAO = OracleMWDAOFactory.getProfileDAO();
    		profileDAO.updatePassword(profileId, password);
    		
    	} catch (DAOException e) {
    		logger.error("Error inserting data.", e);

    		throw new WebApplicationException(e);
    	}
    	
    	resp.setStatus(Response.Status.NO_CONTENT.getStatusCode());

    }
}
