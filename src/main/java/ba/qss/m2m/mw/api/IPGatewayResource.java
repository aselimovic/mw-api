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
import ba.qss.m2m.mw.dao.IPGatewayDAO;
import ba.qss.m2m.mw.dao.IPGatewayTO;
import ba.qss.m2m.mw.dao.OracleMWDAOFactory;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;

@Path("ipGateway")
@Produces("application/json")
public class IPGatewayResource {
	
	private static final String CLASS_NAME =
			IPGatewayResource.class.getName();
    

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
    public IPGatewayTO[] select(

			@QueryParam("filterExpression") String filterExpression 
			,@Size(max=50) @DefaultValue("") @QueryParam("sort") String sort
			,@Min(value=0, message="{ipGateway.sort.size}") @Max(100) @DefaultValue("0") @QueryParam("pageIndex") int pageIndex
			,@Min(0) @Max(1000) @DefaultValue("20") @QueryParam("pageSize") int pageSize) {
    	IPGatewayDAO ipGatewayDAO = null;
		List<IPGatewayTO> ipGateways = null;
		IPGatewayTO criteria = new IPGatewayTO();
        IntValue rowCount = new IntValue(0);
        LoggerContext loggerContext = null;

        loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        StatusPrinter.print(loggerContext);

		try {
           
			ipGatewayDAO = OracleMWDAOFactory.getIPGatewayDAO();
			ipGateways = (List<IPGatewayTO>) (List) ipGatewayDAO.select(
                    criteria, IPGatewayDAO.SELECT_SQL_LIST,
                    filterExpression, sort, pageIndex, pageSize,
                    rowCount);
            
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw new WebApplicationException(e);
        }
		
		return ipGateways.toArray(new IPGatewayTO[ipGateways.size()]);
	}
    
    @GET
	@Path("ipGateway/{ipGatewayId}")
	public IPGatewayTO findIPGatewayByPrimaryKey(
			
			@PathParam("ipGatewayId") int ipGatewayId)
			throws DAOException {
    	IPGatewayDAO ipGatewayDAO = null;
    	IPGatewayTO ipGatewayTO = null;
    	IPGatewayTO criteria = new IPGatewayTO();
    	criteria.setIpGatewayId(ipGatewayId);

        try {
        	ipGatewayDAO = OracleMWDAOFactory.getIPGatewayDAO();
        	ipGatewayTO = (IPGatewayTO)ipGatewayDAO.findByPrimaryKey(criteria, IPGatewayDAO.FIND_BY_PRIMARY_KEY_SQL);
        	
        	
        	if (ipGatewayTO == null) {
        		
        		throw new WebApplicationException(Response.Status.NOT_FOUND);
        	}
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw e;
        }
        
		return ipGatewayTO;
	}
    
    @POST
	@Consumes("application/json")
//	@ValidateRequest
	public IPGatewayTO create(@Valid IPGatewayTO newIPGatewayTO) {
    	IPGatewayDAO ipGatewayDAO = null;
        Object primaryColVal = null;

        try {
        	ipGatewayDAO = OracleMWDAOFactory.getIPGatewayDAO();
        	primaryColVal = ipGatewayDAO.create(newIPGatewayTO,
        			IPGatewayDAO.INSERT_SQL);

        	newIPGatewayTO.setIpGatewayId(
        			((Integer) primaryColVal).intValue());
        			
        } catch (DAOException e) {
        	logger.error("Error inserting data.", e);
        	
        	throw new WebApplicationException(e);
        }
        
		return newIPGatewayTO;
	}
    
    @PUT
    public void update(IPGatewayTO ipGatewayTO) {

    	IPGatewayDAO ipGatewayDAO = null;
    	try {
    		ipGatewayDAO = OracleMWDAOFactory.getIPGatewayDAO();
    		int result = ipGatewayDAO.update(ipGatewayTO, IPGatewayDAO.UPDATE_SQL);
    		
    	} catch (DAOException e) {
    		logger.error("Error inserting data.", e);

    		throw new WebApplicationException(e);
    	}
    	
    	resp.setStatus(Response.Status.NO_CONTENT.getStatusCode());

    }
    
    
    @DELETE
	@Path("ipGateway/{ipGatewayId}")
	public void delete(@PathParam("ipGatewayId") int ipGatewayId) {
		int sc = Response.Status.NO_CONTENT.getStatusCode();
		IPGatewayDAO ipGatewayDAO = null;
		IPGatewayTO ipGatewayTO = null;
		IPGatewayTO criteria = new IPGatewayTO();
		criteria.setIpGatewayId(ipGatewayId);
        
        try {
        	ipGatewayDAO = OracleMWDAOFactory.getIPGatewayDAO();
        	
        	ipGatewayTO = (IPGatewayTO)ipGatewayDAO.findByPrimaryKey(criteria,IPGatewayDAO.FIND_BY_PRIMARY_KEY_SQL);
        	
        	if (ipGatewayTO != null) {
        		ipGatewayDAO.delete(ipGatewayTO, IPGatewayDAO.DELETE_SQL);
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
