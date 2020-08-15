package ba.qss.m2m.mw.api;

import java.text.MessageFormat;
import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;

//import org.jboss.resteasy.spi.validation.ValidateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ba.qss.framework.IntValue;
import ba.qss.framework.dataaccess.DAOException;
import ba.qss.framework.dataaccess.UserRoleDAO;
import ba.qss.framework.dataaccess.UserRoleTO;
import ba.qss.m2m.mw.dao.OracleMWDAOFactory;

@Path("userRoles")
@Produces("application/json")
public class UserRoleResource {

    private static final String CLASS_NAME = UserRoleResource.class.getName();
    public static final Logger logger = LoggerFactory.getLogger(CLASS_NAME);
    
    @GET
    @Path("{userId}")
//    @ValidateRequest
    public UserRoleTO[] select(
    		/*@Context UriInfo uriInfo
			,*/@PathParam("userId") int userId
			,@Pattern(regexp="[\\w\\s,]+") @Size(max=50) @DefaultValue("role_name ASC") @QueryParam("sort") String sort // XXX: Sort key expression: ORDER BY sin(i)
			,@Min(value=0, message="{bindingEntity.sort.size}") @Max(100) @DefaultValue("0") @QueryParam("pageIndex") int pageIndex
			,@Min(0) @Max(1000) @DefaultValue("20") @QueryParam("pageSize") int pageSize) {
		UserRoleDAO userRoleDAO = null;
		List<UserRoleTO> userRoles = null;
		UserRoleTO criteria = new UserRoleTO();
        IntValue rowCount = new IntValue(0);
        String filterExpression = null;

        //java.sql.SQLSyntaxErrorException: ORA-00918: column ambiguously defined
//        criteria.setUserId(userId);
        filterExpression = MessageFormat.format(
        		" (ur.\"USER_ID\" = {0,number,integer}) ", userId);
        
		try {
			userRoleDAO = OracleMWDAOFactory.getUserRoleDAO();
			userRoles = (List<UserRoleTO>) (List) userRoleDAO.select(criteria,
            		UserRoleDAO.SELECT_SQL_LIST, filterExpression, sort,
            		pageIndex, pageSize, rowCount);
            // DAOException
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw new WebApplicationException(e);
        }
        
    	return userRoles.toArray(new UserRoleTO[userRoles.size()]);
    }
}
