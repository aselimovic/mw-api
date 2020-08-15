package ba.qss.m2m.mw.api;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;

//import org.jboss.resteasy.spi.validation.ValidateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import ba.qss.framework.IntValue;
import ba.qss.framework.dataaccess.DAOException;
import ba.qss.m2m.mw.dao.OracleMWDAOFactory;
import ba.qss.m2m.mw.dao.PartnerHierarchyDAO;
import ba.qss.m2m.mw.dao.PartnerHierarchyTO;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;

@Path("partnerhierarchy")
@Produces("application/json")
public class PartnerHierarchyResource {

	private static final String CLASS_NAME = PartnerHierarchyResource.class.getName();
    
    public static final Logger logger = LoggerFactory.getLogger(CLASS_NAME);
    @Context
    private HttpServletResponse resp;
    
    @Context
    public void setServletContext(ServletContext context) {
    	String razvoj = null;
    	razvoj = context.getInitParameter("Razvoj");
    }
    
	@GET
//	@ValidateRequest
	public PartnerHierarchyTO[] select(
			 @QueryParam("filterExpression") String filterExpression 
			,@DefaultValue("p.party_id ASC") @QueryParam("sort") String sort // XXX: Sort key expression: ORDER BY sin(i)
			,@Min(0) @Max(100) @DefaultValue("0") @QueryParam("pageIndex") int pageIndex
			,@Min(0) @Max(1000) @DefaultValue("20") @QueryParam("pageSize") int pageSize) {
		PartnerHierarchyDAO partnerHierarchyDAO = null;
		List<PartnerHierarchyTO> parties = null;
        PartnerHierarchyTO criteria = new PartnerHierarchyTO();
        IntValue rowCount = new IntValue(0);
        LoggerContext loggerContext = null;

       loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
       StatusPrinter.print(loggerContext);
       
		try {
            partnerHierarchyDAO = OracleMWDAOFactory.getPartnerHierarchyDAO();
            parties = (List<PartnerHierarchyTO>) (List) partnerHierarchyDAO.select(criteria, PartnerHierarchyDAO.SELECT_SQL_LIST, filterExpression, sort, pageIndex, pageSize, rowCount);
            // DAOException
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw new WebApplicationException(e);
        } finally {
			// remove() operations should be performed within finally blocks,
			// ensuring their invocation regardless of the execution path of the
			// code.
			MDC.remove("MSISDN");
        }
		
		// Stopping the context will close all appenders attached to loggers
		// defined by the context and stop any active threads. In
		// web-applications, this code would be invoked from within the
		// contextDestroyed method of ServletContextListener 
		//loggerContext.stop();
		
		return parties.toArray(new PartnerHierarchyTO[parties.size()]);
	}
}
