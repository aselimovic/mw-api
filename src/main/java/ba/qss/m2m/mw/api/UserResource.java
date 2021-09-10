package ba.qss.m2m.mw.api;

import java.util.List;

import javax.persistence.OptimisticLockException;
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
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
//import org.jboss.resteasy.spi.validation.ValidateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ba.qss.framework.IntValue;
import ba.qss.framework.dataaccess.DAOException;
import ba.qss.framework.dataaccess.UserDAO;
import ba.qss.framework.dataaccess.UserTO;
import ba.qss.m2m.mw.dao.BindingEntityDAO;
import ba.qss.m2m.mw.dao.BindingEntityTO;
import ba.qss.m2m.mw.dao.OracleMWDAOFactory;
import ba.qss.m2m.mw.dao.ProfileUserDAO;
import ba.qss.m2m.mw.dao.ProfileUserTO;
import ba.qss.m2m.mw.dao.RoutingTableDAO;
import ba.qss.m2m.mw.dao.RoutingTableTO;

@Path("users")
@Produces("application/json")
// JsonIgnoreProperties (Jackson-annotations 2.6.0 API)
// http://fasterxml.github.io/jackson-annotations/javadoc/2.6/
//@JsonIgnoreProperties(ignoreUnknown = true)
public class UserResource {

    private static final String CLASS_NAME = UserResource.class.getName();
    public static final Logger logger = LoggerFactory.getLogger(CLASS_NAME);
    
    // java - JAX-RS --- How to return JSON and HTTP Status code together? - Stack Overflow
    // http://stackoverflow.com/questions/4687271/jax-rs-how-to-return-json-and-http-status-code-together
    @Context
    private HttpServletResponse resp;
    
    @GET
//    @ValidateRequest
    public UserTO[] select(
    		/*@Context UriInfo uriInfo
			,*/
    		@QueryParam("filterExpression") String filterExpression 
    		,@Pattern(regexp="[.\\w\\s,]+") @Size(max=50) @DefaultValue("user_name ASC") @QueryParam("sort") String sort // XXX: Sort key expression: ORDER BY sin(i)
			,@Min(value=0, message="{bindingEntity.sort.size}") @Max(100) @DefaultValue("0") @QueryParam("pageIndex") int pageIndex
			,@Min(0) @Max(1000) @DefaultValue("20") @QueryParam("pageSize") int pageSize) {
		UserDAO userDAO = null;
		List<UserTO> users = null;
        UserTO criteria = new UserTO();
        IntValue rowCount = new IntValue(0);

		try {
			userDAO = OracleMWDAOFactory.getUserDAO();
            users = (List<UserTO>) (List) userDAO.select(criteria,
            		UserDAO.SELECT_SQL_LIST, filterExpression, sort,
            		pageIndex, pageSize, rowCount);
            // DAOException
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw new WebApplicationException(e);
        }
        
    	return users.toArray(new UserTO[users.size()]);
    }
    
	@GET
	@Path("user/{userId}")
	public UserTO findUserByPrimaryKey(@PathParam("userId") int userId)
			throws DAOException {
        UserDAO userDAO = null;
        UserTO userTO = null;

        try {
        	userDAO = OracleMWDAOFactory.getUserDAO();
        	userTO = userDAO.findUserByPrimaryKey(userId, 1/*siteId*/);
        	// DAOException
        	
        	if (userTO == null) {
        		throw new /*NotFoundException()*/WebApplicationException(Response.Status.NOT_FOUND);
        	}
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw e; // Rethrow
        }
        
		return userTO;
	}
	
	@GET
	@Path("findUserByUserName/{userName}")
	public UserTO findUserByUserName(@PathParam("userName") String userName)
			throws DAOException {
        UserDAO userDAO = null;
        UserTO userTO = null;

        try {
        	userDAO = OracleMWDAOFactory.getUserDAO();
        	userTO = userDAO.findUserByUserName(1/*siteId*/, userName);
        	// DAOException
        	
        	if (userTO == null) {
        		throw new /*NotFoundException()*/WebApplicationException(Response.Status.NOT_FOUND);
        	}
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw e; // Rethrow
        }
        
		return userTO;
	}	
	
	@POST
	@Consumes("application/json")
//	@ValidateRequest
	public UserExTO create(@Valid UserExTO newUserExTO) {
        UserDAO userDAO = null;
        Object primaryColVal = null;
    	ProfileUserDAO profileUserDAO = null;
		List<ProfileUserTO> profileUsers = null;
		ProfileUserTO criteria = new ProfileUserTO();
        IntValue rowCount = new IntValue(0);
        UserTO newUserTO = (UserTO)newUserExTO;
        
        try {
        	userDAO = OracleMWDAOFactory.getUserDAO();
        	primaryColVal = userDAO.create(newUserTO, UserDAO.INSERT_SQL);
        	// DAOException
        	
        	newUserTO.setUserId(((Integer) primaryColVal).intValue());
        	// ClassCastException
        	
			profileUserDAO = OracleMWDAOFactory.getProfileUserDAO();
			profileUsers = (List<ProfileUserTO>) (List) profileUserDAO.select(
                    criteria, ProfileUserDAO.SELECT_SQL_LIST,
                    " WHERE \"USER\".user_id=" + newUserTO.getUserId(), null, 0, 20,
                    rowCount);
			
			if ((profileUsers != null) && (profileUsers.size() != 0)) {
				newUserExTO.setProfileId(profileUsers.get(0).getProfileId());
			}
        } catch (DAOException e) {
        	logger.error("Error inserting data.", e);
        	throw new WebApplicationException(e);
        }
        
		return newUserExTO;
	}
	
	@PUT
	@Path("user/{userId}")
	public void update(@PathParam("userId") int userId
    		// RESTful Java with JAX-RS 2.0, Second Edition (O'Reilly Media) -
    		// Chapter 11: Scaling JAX-RS Applications -
			// JAX-RS and Conditional Updates
			/*,@Context Request request*/
			,UserTO userTO) {
		int sc = Response.Status.NO_CONTENT.getStatusCode();
		UserDAO userDAO = null;
		
        try {
        	userDAO = OracleMWDAOFactory.getUserDAO();
        	userDAO.update(userTO, UserDAO.UPDATE_SQL);
        } catch (OptimisticLockException e) {
    		// RESTful Java with JAX-RS 2.0, Second Edition (O'Reilly Media) -
    		// Chapter 11: Scaling JAX-RS Applications
        	sc = Response.Status.PRECONDITION_FAILED.getStatusCode();
        	logger.error("Data shown on form couldn't be saved, because they are changed in the mean time or new version of data was saved by other user. Try to load form again and save the data.", e);
        } catch (DAOException e) {
        	logger.error("Error updating data.", e);
        	// Construct a new instance with a blank message and default HTTP
        	// status code of 500
        	throw new WebApplicationException(e);
        }
        
		resp.setStatus(sc);
	}
	
	@DELETE
	@Path("user/{userId}")
	public void delete(@PathParam("userId") int userId) {
		int sc = Response.Status.NO_CONTENT.getStatusCode();
		UserDAO userDAO = null;
        UserTO userTO = null;
		
        try {
        	userDAO = OracleMWDAOFactory.getUserDAO();
        	userTO = userDAO.findUserByPrimaryKey(
        			userId, 1/*siteId*/);
        	// DAOException
        	
        	if (userTO != null) {
        		userDAO.delete(userTO,
        				UserDAO.DELETE_SQL);
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