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
import ba.qss.m2m.mw.dao.KategorijaDAO;
import ba.qss.m2m.mw.dao.KategorijaTO;
import ba.qss.m2m.mw.dao.OracleMWDAOFactory;
import ba.qss.m2m.mw.dao.PhysicalEnvDAO;
import ba.qss.m2m.mw.dao.PhysicalEnvTO;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;

@Path("kategorija")
@Produces("application/json")
public class KategorijaResource {
	
	private static final String CLASS_NAME =
			KategorijaResource.class.getName();
    

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
    public KategorijaTO[] select(
			@QueryParam("filterExpression") String filterExpression 
			,@Size(max=50) @DefaultValue("") @QueryParam("sort") String sort
			,@Min(value=0, message="{profile.sort.size}") @Max(100) @DefaultValue("0") @QueryParam("pageIndex") int pageIndex
			,@Min(0) @Max(1000) @DefaultValue("20") @QueryParam("pageSize") int pageSize) {
    	KategorijaDAO kategorijaDAO = null;
		List<KategorijaTO> kategorijas = null;
		KategorijaTO criteria = new KategorijaTO();
        IntValue rowCount = new IntValue(0);

		try {           
			kategorijaDAO = OracleMWDAOFactory.getKategorijaDAO();
			kategorijas = (List<KategorijaTO>) (List) kategorijaDAO.select(
                    criteria, KategorijaDAO.SELECT_SQL_LIST,
                    filterExpression, sort, pageIndex, pageSize,
                    rowCount);
            
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw new WebApplicationException(e);
        }
		
		return kategorijas.toArray(new KategorijaTO[kategorijas.size()]);
	}
    
    @GET
	@Path("kategorija/{kategorijaId}")
	public KategorijaTO findKategorijaByPrimaryKey(
			
			@PathParam("kategorijaId") int kategorijaId)
			throws DAOException {
    	KategorijaDAO kategorijaDAO = null;
    	KategorijaTO kategorijaTO = null;
    	KategorijaTO criteria = new KategorijaTO();
    	criteria.setKategorijaId(kategorijaId);

        try {
        	kategorijaDAO = OracleMWDAOFactory.getKategorijaDAO();
        	kategorijaTO = (KategorijaTO)kategorijaDAO.findByPrimaryKey(criteria, KategorijaDAO.FIND_BY_PRIMARY_KEY_SQL);
        	
        	if (kategorijaTO == null) {
        		throw new WebApplicationException(Response.Status.NOT_FOUND);
        	}
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw e;
        }
        
		return kategorijaTO;
	}
    
    @POST
	@Consumes("application/json")
//	@ValidateRequest
	public KategorijaTO create(@Valid KategorijaTO newKategorijaTO) {
    	KategorijaDAO kategorijaDAO = null;
        Object primaryColVal = null;

        try {
        	kategorijaDAO = OracleMWDAOFactory.getKategorijaDAO();
        	primaryColVal = kategorijaDAO.create(newKategorijaTO,
        			KategorijaDAO.INSERT_SQL);

        	newKategorijaTO.setKategorijaId(
        			((Integer) primaryColVal).intValue());
        			
        } catch (DAOException e) {
        	logger.error("Error inserting data.", e);
        	
        	throw new WebApplicationException(e);
        }
        
		return newKategorijaTO;
	}
    
    @DELETE
	@Path("kategorija/{kategorijaId}")
	public void delete(@PathParam("kategorijaId") int kategorijaId) {
		int sc = Response.Status.NO_CONTENT.getStatusCode();
		KategorijaDAO kategorijaDAO = null;
		KategorijaTO kategorijaTO = null;
		KategorijaTO criteria = new KategorijaTO();
		criteria.setKategorijaId(kategorijaId);
        
        try {
        	kategorijaDAO = OracleMWDAOFactory.getKategorijaDAO();
        	
        	kategorijaTO = (KategorijaTO)kategorijaDAO.findByPrimaryKey(criteria,KategorijaDAO.FIND_BY_PRIMARY_KEY_SQL);
        	
        	if (kategorijaTO != null) {
        		kategorijaDAO.delete(kategorijaTO, KategorijaDAO.DELETE_SQL);
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
