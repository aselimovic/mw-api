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
import javax.ws.rs.core.Response;

//import org.jboss.resteasy.spi.validation.ValidateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ba.qss.framework.IntValue;
import ba.qss.framework.dataaccess.DAOException;
import ba.qss.m2m.mw.dao.OracleMWDAOFactory;
import ba.qss.m2m.mw.dao.RoutingTableDAO;
import ba.qss.m2m.mw.dao.RoutingTableTO;

@Path("routingTable")
@Produces("application/json")
public class RoutingTableResource {

    private static final String CLASS_NAME =
    		RoutingTableResource.class.getName();
    public static final Logger logger = LoggerFactory.getLogger(CLASS_NAME);
    
    @Context
    private HttpServletResponse resp;
    
    @GET
//    @ValidateRequest
    public RoutingTableTO[] select(
    		/*@Context UriInfo uriInfo
			,*/@Pattern(regexp="[.\\w\\s,]+") @Size(max=50) @DefaultValue("routing_table_id ASC") @QueryParam("sort") String sort // XXX: Sort key expression: ORDER BY sin(i)
			,@Min(value=0, message="{bindingEntity.sort.size}") @Max(100) @DefaultValue("0") @QueryParam("pageIndex") int pageIndex
			,@Min(0) @Max(1000) @DefaultValue("20") @QueryParam("pageSize") int pageSize) {
		RoutingTableDAO routingTableDAO = null;
		List<RoutingTableTO> routingTable = null;
		RoutingTableTO criteria = new RoutingTableTO();
        IntValue rowCount = new IntValue(0);

		try {
			routingTableDAO = OracleMWDAOFactory.getRoutingTableDAO();
			routingTable = (List<RoutingTableTO>) (List) routingTableDAO.select(
					criteria, RoutingTableDAO.SELECT_SQL_LIST, null/*filterExpression*/,
					sort, pageIndex, pageSize, rowCount);
            // DAOException
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw new WebApplicationException(e);
        }
        
    	return routingTable.toArray(new RoutingTableTO[routingTable.size()]);
    }
    
	@GET
	@Path("routingTableDetail/{routingTableId}")
	public RoutingTableTO findBindingEntityByPrimaryKey(
			@PathParam("routingTableId") int routingTableId)
					throws DAOException {
        RoutingTableDAO routingTableDAO = null;
        RoutingTableTO routingTableTO = null;

        try {
        	routingTableDAO = OracleMWDAOFactory.getRoutingTableDAO();
        	routingTableTO = routingTableDAO.findRoutingTableByPrimaryKey(routingTableId);
        	// DAOException
        	
        	if (routingTableTO == null) {
        		throw new /*NotFoundException()*/WebApplicationException(Response.Status.NOT_FOUND);
        	}
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw e; // Rethrow
        }
        
		return routingTableTO;
	}
	
	@POST
	@Consumes("application/json")
//	@ValidateRequest
	public RoutingTableTO create(@Valid RoutingTableTO newRoutingTableTO) {
        RoutingTableDAO routingTableDAO = null;
        Object primaryColVal = null;

        try {
        	routingTableDAO = OracleMWDAOFactory.getRoutingTableDAO();
        	primaryColVal = routingTableDAO.create(newRoutingTableTO, RoutingTableDAO.INSERT_SQL);
        	// DAOException
        	
        	newRoutingTableTO.setRoutingTableId(((Integer) primaryColVal).intValue());
        	// ClassCastException
        } catch (DAOException e) {
        	logger.error("Error inserting data.", e);
        	throw new WebApplicationException(e);
        }
		
		return newRoutingTableTO;		
	}
	
	@PUT
    public void update(RoutingTableTO routingTableTO) {
		int sc = Response.Status.NO_CONTENT.getStatusCode();
		RoutingTableDAO routingTableDAO = null;
		
    	try {
    		routingTableDAO = OracleMWDAOFactory.getRoutingTableDAO();
    		routingTableDAO.update(routingTableTO, RoutingTableDAO.UPDATE_SQL);
    	} catch (DAOException e) {
        	logger.error("Error updating data.", e);
        	// Construct a new instance with a blank message and default HTTP
        	// status code of 500        	
    		throw new WebApplicationException(e);
    	}
    	
    	resp.setStatus(sc);
    }
	
	@DELETE
	@Path("routingTable/{routingTableId}")
	public void delete(@PathParam("routingTableId") int routingTableId) {
		int sc = Response.Status.NO_CONTENT.getStatusCode();
		RoutingTableDAO routingTableDAO = null;
        RoutingTableTO routingTableTO = null;
		
        try {
        	routingTableDAO = OracleMWDAOFactory.getRoutingTableDAO();
        	routingTableTO = routingTableDAO.findRoutingTableByPrimaryKey(
        			routingTableId);
        	// DAOException
        	
        	if (routingTableTO != null) {
        		routingTableDAO.delete(routingTableTO,
        				RoutingTableDAO.DELETE_SQL);
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
