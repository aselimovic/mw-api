package ba.qss.m2m.mw.api;

import java.util.List;

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

//import org.jboss.resteasy.spi.validation.ValidateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ba.qss.framework.IntValue;
import ba.qss.framework.dataaccess.DAOException;
import ba.qss.m2m.mw.dao.OracleMWDAOFactory;
import ba.qss.m2m.mw.dao.XsltStylesheetDAO;
import ba.qss.m2m.mw.dao.XsltStylesheetTO;

@Path("xsltStylesheets")
@Produces("application/json")
public class XsltStylesheetResource {

    private static final String CLASS_NAME =
    		XsltStylesheetResource.class.getName();
    public static final Logger logger = LoggerFactory.getLogger(CLASS_NAME);
    
    @GET
//    @ValidateRequest
    public XsltStylesheetTO[] select(
    		/*@Context UriInfo uriInfo
			,*/@Pattern(regexp="[\\w\\s,]+") @Size(max=50) @DefaultValue("xslt_stylesheet_id ASC") @QueryParam("sort") String sort // XXX: Sort key expression: ORDER BY sin(i)
			,@Min(value=0, message="{bindingEntity.sort.size}") @Max(100) @DefaultValue("0") @QueryParam("pageIndex") int pageIndex
			,@Min(0) @Max(1000) @DefaultValue("20") @QueryParam("pageSize") int pageSize) {
    	XsltStylesheetDAO xsltStylesheetDAO = null;
		List<XsltStylesheetTO> xsltStylesheets = null;
		XsltStylesheetTO criteria = new XsltStylesheetTO();
        IntValue rowCount = new IntValue(0);

		try {
			xsltStylesheetDAO = OracleMWDAOFactory.getXsltSchemaDAO();
			xsltStylesheets = (List<XsltStylesheetTO>) (List) xsltStylesheetDAO.select(
					criteria, XsltStylesheetDAO.SELECT_SQL_LIST, null/*filterExpression*/,
					sort, pageIndex, pageSize, rowCount);
            // DAOException
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw new WebApplicationException(e);
        }
        
    	return xsltStylesheets.toArray(new XsltStylesheetTO[xsltStylesheets.size()]);
    }
}