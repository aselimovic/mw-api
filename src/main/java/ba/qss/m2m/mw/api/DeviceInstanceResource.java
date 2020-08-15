package ba.qss.m2m.mw.api;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.OptimisticLockException;
import javax.servlet.ServletContext;
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

import org.codehaus.jackson.JsonParser;
//import org.jboss.resteasy.spi.validation.ValidateRequest;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import ba.qss.framework.IntValue;
import ba.qss.framework.dataaccess.DAOException;
import ba.qss.m2m.mw.dao.DeviceInstanceDAO;
import ba.qss.m2m.mw.dao.DeviceInstanceTO;
import ba.qss.m2m.mw.dao.DeviceTO;
import ba.qss.m2m.mw.dao.OracleMWDAOFactory;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;

@Path("deviceInstance")
@Produces("application/json")
public class DeviceInstanceResource {
	
	private static final String CLASS_NAME =
			DeviceInstanceResource.class.getName();
    

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
	public DeviceInstanceTO[] select(
			@QueryParam("filterExpression") String filterExpression 
			,@Size(max=50) @DefaultValue("") @QueryParam("sort") String sort
			,@Min(value=0, message="{deviceInstance.sort.size}") @Max(100) @DefaultValue("0") @QueryParam("pageIndex") int pageIndex
			,@Min(0) @Max(1000) @DefaultValue("20") @QueryParam("pageSize") int pageSize) {
    	
    	DeviceInstanceDAO deviceInstanceDAO = null;
		List<DeviceInstanceTO> deviceInstance = null;
		DeviceInstanceTO criteria = new DeviceInstanceTO();
        IntValue rowCount = new IntValue(0);
        LoggerContext loggerContext = null;

        
        loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        StatusPrinter.print(loggerContext);
        
        
		try {
           System.out.println("select:"+filterExpression);
			deviceInstanceDAO = OracleMWDAOFactory.getDeviceInstanceDAO();
			deviceInstance = (List<DeviceInstanceTO>) (List) deviceInstanceDAO.select(
                    criteria, DeviceInstanceDAO.SELECT_SQL_LIST,
                    filterExpression, sort, pageIndex, pageSize,
                    rowCount);
            
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw new WebApplicationException(e);
        }
		
		return deviceInstance.toArray(new DeviceInstanceTO[deviceInstance.size()]);
	}
    
    @GET
    @Path("profile/{profileId}")
	public DeviceInstanceTO[] selectByProfile(
			@PathParam("profileId") int profileId 
			,@Size(max=50) @DefaultValue("") @QueryParam("sort") String sort
			,@Min(value=0, message="{deviceInstance.sort.size}") @Max(100) @DefaultValue("0") @QueryParam("pageIndex") int pageIndex
			,@Min(0) @Max(1000) @DefaultValue("20") @QueryParam("pageSize") int pageSize) {
    	
    	DeviceInstanceDAO deviceInstanceDAO = null;
		List<DeviceInstanceTO> deviceInstance = null;
		DeviceInstanceTO criteria = new DeviceInstanceTO();
        IntValue rowCount = new IntValue(0);
        LoggerContext loggerContext = null;

        String filterExpression = " inner join ip_gateway on ip_gateway.ip_gateway_id=di.ip_gateway_id where ip_gateway.profile_id="+profileId;
        
		try {
           
			deviceInstanceDAO = OracleMWDAOFactory.getDeviceInstanceDAO();
			deviceInstance = (List<DeviceInstanceTO>) (List) deviceInstanceDAO.select(
                    criteria, DeviceInstanceDAO.SELECT_SQL_LIST,
                    filterExpression, sort, pageIndex, pageSize,
                    rowCount);
            
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw new WebApplicationException(e);
        }
		
		return deviceInstance.toArray(new DeviceInstanceTO[deviceInstance.size()]);
	}
    
    
    
    
    
    
    @GET
    @Path("potrosnja/{potrosacId}/{partnerId}")
	public DeviceInstanceTO[] selectZaPotrosnju(
			@PathParam("potrosacId") String potrosacId,
			@PathParam("partnerId") int partnerId 
			,@Size(max=50) @DefaultValue("") @QueryParam("sort") String sort
			,@Min(value=0, message="{deviceInstance.sort.size}") @Max(100) @DefaultValue("0") @QueryParam("pageIndex") int pageIndex
			,@Min(0) @Max(1000) @DefaultValue("20") @QueryParam("pageSize") int pageSize) {
    	
    	DeviceInstanceDAO deviceInstanceDAO = null;
		List<DeviceInstanceTO> deviceInstance = null;
		DeviceInstanceTO criteria = new DeviceInstanceTO();
        IntValue rowCount = new IntValue(0);
        LoggerContext loggerContext = null;

        String filterExpression = " inner join device on di.device_id = device.device_id where di.party_id in ("+ potrosacId + ") and device.party_id = " + partnerId;
        
		try {
           
			deviceInstanceDAO = OracleMWDAOFactory.getDeviceInstanceDAO();
			deviceInstance = (List<DeviceInstanceTO>) (List) deviceInstanceDAO.select(
                    criteria, DeviceInstanceDAO.SELECT_SQL_LIST,
                    filterExpression, sort, pageIndex, pageSize,
                    rowCount);
            
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw new WebApplicationException(e);
        }
		
		return deviceInstance.toArray(new DeviceInstanceTO[deviceInstance.size()]);
	}
    
    
    
    
    
    
    @GET
    @Path("profilefiltered/{profileId}")
	public DeviceInstanceTO[] selectByProfileFiltered(
			@PathParam("profileId") int profileId
			,@QueryParam("filterExpression") String filterExpression
			,@Size(max=50) @DefaultValue("") @QueryParam("sort") String sort
			,@Min(value=0, message="{deviceInstance.sort.size}") @Max(100) @DefaultValue("0") @QueryParam("pageIndex") int pageIndex
			,@Min(0) @Max(1000) @DefaultValue("20") @QueryParam("pageSize") int pageSize) {
    	
    	DeviceInstanceDAO deviceInstanceDAO = null;
		List<DeviceInstanceTO> deviceInstance = null;
		DeviceInstanceTO criteria = new DeviceInstanceTO();
        IntValue rowCount = new IntValue(0);
        LoggerContext loggerContext = null;
        if(filterExpression==null){
        	filterExpression="";
        }

        String filExp = " inner join ip_gateway on ip_gateway.ip_gateway_id=di.ip_gateway_id where ip_gateway.profile_id="+profileId+" "+filterExpression;
        
		try {
           System.out.println(" FilterExp:"+filExp);
			deviceInstanceDAO = OracleMWDAOFactory.getDeviceInstanceDAO();
			deviceInstance = (List<DeviceInstanceTO>) (List) deviceInstanceDAO.select(
                    criteria, DeviceInstanceDAO.SELECT_SQL_LIST,
                    filExp, sort, pageIndex, pageSize,
                    rowCount);
            
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw new WebApplicationException(e);
        }
		
		return deviceInstance.toArray(new DeviceInstanceTO[deviceInstance.size()]);
	}
    
    @GET
   	@Path("deviceInstance/{deviceInstanceId}")
   	public DeviceInstanceTO findDeviceInstanceByPrimaryKey(
   			
   			@PathParam("deviceInstanceId") int deviceInstanceId)
   			throws DAOException {
    	DeviceInstanceDAO deviceInstanceDAO = null;
    	DeviceInstanceTO deviceInstanceTO = null;
    	DeviceInstanceTO criteria = new DeviceInstanceTO();
       	criteria.setDeviceInstanceId(deviceInstanceId);

           try {
        	   deviceInstanceDAO = OracleMWDAOFactory.getDeviceInstanceDAO();
        	   deviceInstanceTO = (DeviceInstanceTO)deviceInstanceDAO.findByPrimaryKey(criteria, DeviceInstanceDAO.FIND_BY_PRIMARY_KEY_SQL);
           	
           	
           	if (deviceInstanceTO == null) {
           		
           		throw new WebApplicationException(Response.Status.NOT_FOUND);
           	}
           } catch (DAOException e) {
           	logger.error(null, e);
           	throw e;
           }
           
   		return deviceInstanceTO;
   	}
    
    @POST
	@Consumes("application/json")
	//@ValidateRequest
	public DeviceInstanceTO create(@Valid DeviceInstanceTO newDeviceInstanceTO) {
    	DeviceInstanceDAO deviceInstanceDAO = null;
        Object primaryColVal = null;

        try {
        	deviceInstanceDAO = OracleMWDAOFactory.getDeviceInstanceDAO();
        	primaryColVal = deviceInstanceDAO.create(newDeviceInstanceTO,
        			DeviceInstanceDAO.INSERT_SQL);

        	newDeviceInstanceTO.setDeviceInstanceId(
        			((Integer) primaryColVal).intValue());
        			
        } catch (DAOException e) {
        	logger.error("Error inserting data.", e);
        	
        	throw new WebApplicationException(e);
        }
        
		return newDeviceInstanceTO;
	}
    
    @PUT
    public void update(DeviceInstanceTO deviceInstanceTO) {

    	DeviceInstanceDAO deviceInstanceDAO = null;
    	try {
    		deviceInstanceDAO = OracleMWDAOFactory.getDeviceInstanceDAO();
    		int result = deviceInstanceDAO.update(deviceInstanceTO, DeviceInstanceDAO.UPDATE_SQL);
    		
    	} catch (DAOException e) {
    		logger.error("Error inserting data.", e);

    		throw new WebApplicationException(e);
    	}
    	
    	resp.setStatus(Response.Status.NO_CONTENT.getStatusCode());

    }
    
    @PUT
    @Path("name/")
    public org.json.simple.JSONObject updateName(org.json.simple.JSONObject deviceInstance) {
    	
    	DeviceInstanceDAO deviceInstanceDAO = null;
    	org.json.simple.JSONObject jsonObject=new JSONObject();
    	jsonObject.put("Status","true");
    	try {
    		deviceInstanceDAO = OracleMWDAOFactory.getDeviceInstanceDAO();
    		int diId = (Integer)deviceInstance.get("deviceInstanceId");
    		String newName = (String)deviceInstance.get("name");
    		System.out.println("UpdateName called:"+deviceInstance.get("deviceInstanceId"));

    		//get di list for device
    		List<DeviceInstanceTO> listDITO = getDeviceListForId(diId);
    		//update name
    		if(listDITO.size()>0){
    			for(DeviceInstanceTO dito:listDITO){
    				dito.setName(newName);
    				int result = deviceInstanceDAO.update(dito, DeviceInstanceDAO.UPDATE_SQL);
    				System.out.println("ZwaveDevices update name result:"+result+" with name:"+newName);
    			}
    		}

    	} catch (DAOException e) {
    		logger.error("Error inserting data.", e);

    		throw new WebApplicationException(e);
    	}

    	return jsonObject;

    }
    
    @PUT
    @Path("type/")
    public org.json.simple.JSONObject updateType(org.json.simple.JSONObject deviceInstance) {
    	
    	DeviceInstanceDAO deviceInstanceDAO = null;
    	org.json.simple.JSONObject jsonObject=new JSONObject();
    	jsonObject.put("Status","true");
    	try {
    		deviceInstanceDAO = OracleMWDAOFactory.getDeviceInstanceDAO();
    		int diId = (Integer)deviceInstance.get("deviceInstanceId");
    		int dId = (Integer)deviceInstance.get("deviceId");
    		System.out.println("UpdateName called:"+deviceInstance.get("deviceInstanceId"));

    		//get di list for device
    		List<DeviceInstanceTO> listDITO = getDeviceListForId(diId);
    		//update deviceId
    		if(listDITO.size()>0){
    			DeviceTO deviceTO = new DeviceTO();
    			deviceTO.setDeviceId(dId);
    			for(DeviceInstanceTO dito:listDITO){
    				if(dito.getDeviceId().getDeviceId()!=8 && dito.getDeviceId().getDeviceId()!=3){
    					//baterija i kamera se ne mjenjaju
    					dito.setDeviceId(deviceTO);
    					int result = deviceInstanceDAO.update(dito, DeviceInstanceDAO.UPDATE_SQL);
    					System.out.println("ZwaveDevices update name result:"+result+" with deviceId:"+dId);
    				}
    			}
    		}
    	} catch (DAOException e) {
    		logger.error("Error inserting data.", e);
    		throw new WebApplicationException(e);
    	}


    	return jsonObject;

    }
    
    
    @DELETE
	@Path("deviceInstance/{deviceInstanceId}")
	public void delete(@PathParam("deviceInstanceId") int deviceInstanceId) {
		int sc = Response.Status.NO_CONTENT.getStatusCode();
		DeviceInstanceDAO deviceInstanceDAO = null;
		DeviceInstanceTO deviceInstanceTO = null;
		DeviceInstanceTO criteria = new DeviceInstanceTO();
		criteria.setDeviceInstanceId(deviceInstanceId);
        
        try {
        	deviceInstanceDAO = OracleMWDAOFactory.getDeviceInstanceDAO();
        	
        	deviceInstanceTO = (DeviceInstanceTO)deviceInstanceDAO.findByPrimaryKey(criteria,DeviceInstanceDAO.FIND_BY_PRIMARY_KEY_SQL);
        	
        	if (deviceInstanceTO != null) {
        		deviceInstanceDAO.delete(deviceInstanceTO, DeviceInstanceDAO.DELETE_SQL);
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
    
    private List<DeviceInstanceTO> getDeviceListForId(int deviceInstanceId){
    	List<DeviceInstanceTO> deviceInstance = null;
    	DeviceInstanceDAO deviceInstanceDAO = null;
    	DeviceInstanceTO deviceInstanceTO = null;
    	DeviceInstanceTO criteria = new DeviceInstanceTO();
    	criteria.setDeviceInstanceId(deviceInstanceId);

    	try {
    		deviceInstanceDAO = OracleMWDAOFactory.getDeviceInstanceDAO();
    		deviceInstanceTO = (DeviceInstanceTO)deviceInstanceDAO.findByPrimaryKey(criteria, DeviceInstanceDAO.FIND_BY_PRIMARY_KEY_SQL);


    		if (deviceInstanceTO != null) {
    			if(deviceInstanceTO.getDeviceId().getDeviceId()==3){
    				//deviceInstance=new ArrayList<DeviceInstanceTO>();
    				deviceInstance.add(deviceInstanceTO);
    			} else {
    				String zWaveName=deviceInstanceTO.getZwaveName();
    				String[] idParts = zWaveName.split("-");
    				if(idParts.length>=3){
    					String filterZwaveId = idParts[0];
    					System.out.println("ZwaveDevices base name:"+filterZwaveId);
    					IntValue rowCount = new IntValue(0);
    					String filterExp=" where ip_gateway_id="+deviceInstanceTO.getIpGatewayId().getIpGatewayId()+" and zwave_name like '%"+filterZwaveId+"%'";
    					System.out.println("Update:"+filterExp);
    					deviceInstance = (List<DeviceInstanceTO>) (List) deviceInstanceDAO.select(
    							new DeviceInstanceTO(), DeviceInstanceDAO.SELECT_SQL_LIST,
    							filterExp, "", 0, 20,
    							rowCount);
//    					for(DeviceInstanceTO dito:deviceInstance){
//    						if(dito.getZwaveName().startsWith(filterZwaveId)){
//    							deviceInstanceReturn.add(dito);
//    						}
//    					}
    					System.out.println("ZwaveDevices for change:"+deviceInstance.size());
    				}
    			}
    		}
    	} catch (DAOException e) {
    		logger.error(null, e);
    	}
    	return deviceInstance;
    }

}
