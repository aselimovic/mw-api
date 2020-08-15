package ba.qss.m2m.mw.api;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;

//import org.jboss.resteasy.spi.validation.ValidateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ba.qss.framework.IntValue;
import ba.qss.framework.dataaccess.DAOException;
import ba.qss.m2m.mw.dao.MessageDAO;
import ba.qss.m2m.mw.dao.MessageTO;
import ba.qss.m2m.mw.dao.OracleMWDAOFactory;

@Path("message")
@Produces("application/json")
public class MessageResource {
	
	private static final String CLASS_NAME =
			MessageResource.class.getName();
    

    public static final Logger logger = LoggerFactory.getLogger(CLASS_NAME);

    @Context
    private HttpServletResponse resp;
    
    @Context
    public void setServletContext(ServletContext context) {
    	String razvoj = null;

    	razvoj = context.getInitParameter("Razvoj");
    }
    
    @GET
    @Path("stanica/{stanicaId}")
//    @ValidateRequest
    public MessageTO[] selectByStanica(
    		@PathParam("stanicaId") int stanicaId 
			,@Size(max=50) @DefaultValue("") @QueryParam("sort") String sort
			,@Min(value=0, message="{message.sort.size}") @Max(100) @DefaultValue("0") @QueryParam("pageIndex") int pageIndex
			,@Min(0) @Max(1000) @DefaultValue("20") @QueryParam("pageSize") int pageSize) {
    	MessageDAO messageDAO = null;
		List<MessageTO> messages = null;
		MessageTO criteria = new MessageTO();
        IntValue rowCount = new IntValue(0);

        String filterExpression="m.binding_entity_id = 35 and m.message_payload like '%<deviceInstanceId>"+stanicaId+"</deviceInstanceId>%'";
        sort="message_id desc";
        
		try {           
			messageDAO = OracleMWDAOFactory.getMessageDAO();
			messages = (List<MessageTO>) (List) messageDAO.select(
                    criteria, MessageDAO.SELECT_SQL_LIST,
                    filterExpression, sort, pageIndex, pageSize,
                    rowCount);
            
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw new WebApplicationException(e);
        }
		
		return messages.toArray(new MessageTO[messages.size()]);
	}

}
