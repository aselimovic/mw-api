package ba.qss.m2m.mw.api;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ba.qss.framework.dataaccess.DAOException;
import ba.qss.m2m.mw.dao.LiferayUserDAO;
import ba.qss.m2m.mw.dao.LiferayUserTO;
import ba.qss.m2m.mw.dao.OracleMWDAOFactory;

@Path("liferayUser")
@Produces("application/json")
public class LiferayUserResource {
	
	private static final String CLASS_NAME =
			LiferayUserResource.class.getName();
    

    public static final Logger logger = LoggerFactory.getLogger(CLASS_NAME);

    @Context
    private HttpServletResponse resp;
    
    @Context
    public void setServletContext(ServletContext context) {
    	String razvoj = null;

    	razvoj = context.getInitParameter("Razvoj");
    }
    
    @GET
	@Path("liferayUser/{liferayUserId}")
	public LiferayUserTO findLiferayUserByPrimaryKey(
			
			@PathParam("liferayUserId") int liferayUserId)
			throws DAOException {
    	LiferayUserDAO liferayUserDAO = null;
    	LiferayUserTO liferayUserTO = null;
    	LiferayUserTO criteria = new LiferayUserTO();
    	criteria.setUserId(liferayUserId);

        try {
        	liferayUserDAO = OracleMWDAOFactory.getLiferayUserDAO();
        	liferayUserTO = (LiferayUserTO)liferayUserDAO.findByPrimaryKey(criteria, LiferayUserDAO.FIND_BY_PRIMARY_KEY_SQL);
        	
        	if (liferayUserTO == null) {
        		throw new WebApplicationException(Response.Status.NOT_FOUND);
        	}
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw e;
        }
        
		return liferayUserTO;
	}
    
    @PUT
    public void update(LiferayUserTO liferayUserTO) {

    	LiferayUserDAO liferayUserDAO = null;
    	try {
    		liferayUserDAO = OracleMWDAOFactory.getLiferayUserDAO();
    		int result = liferayUserDAO.update(liferayUserTO, LiferayUserDAO.UPDATE_SQL);
    		
    	} catch (DAOException e) {
    		logger.error("Error inserting data.", e);

    		throw new WebApplicationException(e);
    	}
    	
    	resp.setStatus(Response.Status.NO_CONTENT.getStatusCode());

    }

}
