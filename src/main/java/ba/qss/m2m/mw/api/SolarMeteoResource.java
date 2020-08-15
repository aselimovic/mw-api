package ba.qss.m2m.mw.api;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import javax.ws.rs.Consumes;
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
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ba.qss.framework.IntValue;
import ba.qss.framework.dataaccess.DAOException;
import ba.qss.framework.net.HttpClient;
import ba.qss.m2m.mw.dao.DeviceInstanceDAO;
import ba.qss.m2m.mw.dao.DeviceInstanceTO;
import ba.qss.m2m.mw.dao.MessageDAO;
import ba.qss.m2m.mw.dao.MessageTO;
import ba.qss.m2m.mw.dao.OracleMWDAOFactory;
import ba.qss.m2m.mw.dao.SolarMeteoOcitanjeTO;
import ba.qss.m2m.mw.dao.SolarMeteoReadingDAO;
import ba.qss.m2m.mw.dao.SolarMeteoReadingTO;
import ba.qss.m2m.mw.dao.SolarMeteoTO;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;

@Path("solarmeteo")
@Produces("application/json")
public class SolarMeteoResource {
	
	private static final String CLASS_NAME =
			PhysicalEnvResource.class.getName();
    

    public static final Logger logger = LoggerFactory.getLogger(CLASS_NAME);

    @Context
    private HttpServletResponse resp;
    
    @Context
    public void setServletContext(ServletContext context) {
    	String razvoj = null;

    	razvoj = context.getInitParameter("Razvoj");
    }
    
    @GET
	@Path("stanice")
//    @ValidateRequest
    public SolarMeteoTO[] selectStanica(
			@Size(max=50) @DefaultValue("") @QueryParam("sort") String sort
			,@Min(value=0, message="{solarmeteo.sort.size}") @Max(100) @DefaultValue("0") @QueryParam("pageIndex") int pageIndex
			,@Min(0) @Max(1000) @DefaultValue("20") @QueryParam("pageSize") int pageSize) {
    	
    	DeviceInstanceDAO deviceInstanceDAO = null;
    	List<DeviceInstanceTO> deviceInstances = null;
		List<SolarMeteoTO> solarMeteos = null;
		DeviceInstanceTO criteria = new DeviceInstanceTO();
        IntValue rowCount = new IntValue(0);
        LoggerContext loggerContext = null;
        solarMeteos = new ArrayList<SolarMeteoTO>();

        String filterExpression = "where di.device_id=50";

		try {
			deviceInstanceDAO = OracleMWDAOFactory.getDeviceInstanceDAO();
			deviceInstances = (List<DeviceInstanceTO>) (List) deviceInstanceDAO.select(
                    criteria, DeviceInstanceDAO.SELECT_SQL_LIST,
                    filterExpression, sort, pageIndex, pageSize,
                    rowCount);
			
			for(DeviceInstanceTO deviceInstanceTO:deviceInstances){
				SolarMeteoTO solarMeteoTO = new SolarMeteoTO();
				solarMeteoTO.setSolarMeteoStanicaId(deviceInstanceTO.getDeviceInstanceId());
				solarMeteoTO.setSolarMeteoStanica(deviceInstanceTO.getName());
				solarMeteoTO.setIpAddress(deviceInstanceTO.getIpAddress());
				solarMeteos.add(solarMeteoTO);
			}
            
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw new WebApplicationException(e);
        }
		
		return solarMeteos.toArray(new SolarMeteoTO[solarMeteos.size()]);
	}
    
    @GET
	@Path("ocitanja/{stanicaId}")
//    @ValidateRequest
    public SolarMeteoOcitanjeTO[] selectOcitanjaStanice(
    		@PathParam("stanicaId") int stanicaId
			,@Min(value=0, message="{message.sort.size}") @Max(100) @DefaultValue("1") @QueryParam("page") int page){
    	
//    	MessageDAO messageDAO = null;
//		List<MessageTO> messages = null;
//		MessageTO criteria = new MessageTO();
//        IntValue rowCount = new IntValue(0);
//        List<SolarMeteoOcitanjeTO> solarMeteoOcitanjes = new ArrayList<SolarMeteoOcitanjeTO>();
//
//        String filterExpression="m.binding_entity_id = 35 and m.message_payload like '%<deviceInstanceId>"+stanicaId+"</deviceInstanceId>%'";
//        String sort="message_id desc";
//                
//		try {           
//			messageDAO = OracleMWDAOFactory.getMessageDAO();
//			messages = (List<MessageTO>) (List) messageDAO.select(
//                    criteria, MessageDAO.SELECT_SQL_LIST,
//                    filterExpression, sort, page-1, 20,
//                    rowCount);
//			
//			for(MessageTO messageTO:messages){
//				SolarMeteoOcitanjeTO ocitanje = ucitajOcitanjeIzPoruke(messageTO);
//				if(ocitanje!=null){
//					ocitanje.setSolarMeteoStanicaId(stanicaId);
//					solarMeteoOcitanjes.add(ocitanje);
//				}
//			}
//            
//        } catch (DAOException e) {
//        	logger.error(null, e);
//        	throw new WebApplicationException(e);
//        }
//		
//		return solarMeteoOcitanjes.toArray(new SolarMeteoOcitanjeTO[solarMeteoOcitanjes.size()]);
		
		SolarMeteoReadingDAO solarMeteoReadingDAO = null;
		List<SolarMeteoReadingTO> solarMeteoReadings = null;
		SolarMeteoReadingTO criteria = new SolarMeteoReadingTO();
        IntValue rowCount = new IntValue(0);
        List<SolarMeteoOcitanjeTO> solarMeteoOcitanjes = new ArrayList<SolarMeteoOcitanjeTO>();

        String filterExpression="where solar_meteo_reading.device_instance_id = "+stanicaId;
        String sort = "solar_meteo_reading.solar_meteo_reading_id DESC";
        
		try {           
			solarMeteoReadingDAO = OracleMWDAOFactory.getSolarMeteoReadingDAO();
			solarMeteoReadings = (List<SolarMeteoReadingTO>) (List) solarMeteoReadingDAO.select(
                    criteria, SolarMeteoReadingDAO.SELECT_SQL_LIST,
                    filterExpression, sort, page-1, 20,
                    rowCount);
			
			for(SolarMeteoReadingTO solarMeteoReadingTO:solarMeteoReadings){
				SolarMeteoOcitanjeTO ocitanje = ucitajOcitanjeIzReadinga(solarMeteoReadingTO);
				if(ocitanje!=null){
					ocitanje.setSolarMeteoStanicaId(stanicaId);
					solarMeteoOcitanjes.add(ocitanje);
				}
			}
            
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw new WebApplicationException(e);
        }
		
		return solarMeteoOcitanjes.toArray(new SolarMeteoOcitanjeTO[solarMeteoOcitanjes.size()]);
    	
    }
    
    @GET
	@Path("rawReadings/{stanicaId}")
//    @ValidateRequest
    public SolarMeteoReadingTO[] selectRawReadingsStanice(
    		@PathParam("stanicaId") int stanicaId
			,@Min(value=0, message="{message.sort.size}") @Max(100) @DefaultValue("1") @QueryParam("page") int page){

		SolarMeteoReadingDAO solarMeteoReadingDAO = null;
		List<SolarMeteoReadingTO> solarMeteoReadings = null;
		SolarMeteoReadingTO criteria = new SolarMeteoReadingTO();
        IntValue rowCount = new IntValue(0);
        
        String filterExpression="where solar_meteo_reading.device_instance_id = "+stanicaId;
        String sort = "solar_meteo_reading.solar_meteo_reading_id DESC";
        
		try {           
			solarMeteoReadingDAO = OracleMWDAOFactory.getSolarMeteoReadingDAO();
			solarMeteoReadings = (List<SolarMeteoReadingTO>) (List) solarMeteoReadingDAO.select(
                    criteria, SolarMeteoReadingDAO.SELECT_SQL_LIST,
                    filterExpression, sort, page-1, 20,
                    rowCount);

        } catch (DAOException e) {
        	logger.error(null, e);
        	throw new WebApplicationException(e);
        }
		
		return solarMeteoReadings.toArray(new SolarMeteoReadingTO[solarMeteoReadings.size()]);
    	
    }
    
    @GET
	@Path("zadnjeOcitanje/{stanicaId}")
    public SolarMeteoOcitanjeTO selectZadnjeOcitanjeStanice(
    		@PathParam("stanicaId") int stanicaId){
    	
    	DeviceInstanceDAO deviceInstanceDAO = null;
    	DeviceInstanceTO deviceInstanceTO = null;
    	DeviceInstanceTO criteriaDevice = new DeviceInstanceTO();
    	criteriaDevice.setDeviceInstanceId(stanicaId);

    	try {
    		deviceInstanceDAO = OracleMWDAOFactory.getDeviceInstanceDAO();
    		deviceInstanceTO = (DeviceInstanceTO)deviceInstanceDAO.findByPrimaryKey(criteriaDevice, DeviceInstanceDAO.FIND_BY_PRIMARY_KEY_SQL);
    		if (deviceInstanceTO != null) {
    			Date date=new Date();
    			HttpClient client = new HttpClient();
    			client.setReadTimeout(5000);
    			String url = "http://172.30.21.163:7777/?deviceId="+stanicaId+"&ipAddress="+deviceInstanceTO.getIpAddress()+"&port="+deviceInstanceTO.getSerialNumber();
    			String response=client.readAllData(url, "GET", null);
    			Thread.sleep(3000);
    		}
    	} catch (DAOException e) {
    		logger.error(null, e);
    	} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}    	
    	
//    	MessageDAO messageDAO = null;
//		List<MessageTO> messages = null;
//		MessageTO criteria = new MessageTO();
//        IntValue rowCount = new IntValue(0);
//        List<SolarMeteoOcitanjeTO> solarMeteoOcitanjes = new ArrayList<SolarMeteoOcitanjeTO>();
//
//        String filterExpression="m.binding_entity_id = 35 and m.message_payload like '%<deviceInstanceId>"+stanicaId+"</deviceInstanceId>%'";
//        String sort="message_id desc";
//                
//		try {           
//			messageDAO = OracleMWDAOFactory.getMessageDAO();
//			messages = (List<MessageTO>) (List) messageDAO.select(
//                    criteria, MessageDAO.SELECT_SQL_LIST,
//                    filterExpression, sort, 0, 20,
//                    rowCount);
//			
//			for(MessageTO messageTO:messages){
//				SolarMeteoOcitanjeTO ocitanje = ucitajOcitanjeIzPoruke(messageTO);
//				if(ocitanje!=null){
//					ocitanje.setSolarMeteoStanicaId(stanicaId);
//					return ocitanje;
//				}
//			}
//            
//        } catch (DAOException e) {
//        	logger.error(null, e);
//        	throw new WebApplicationException(e);
//        }
//		
//		throw new WebApplicationException(Response.Status.NOT_FOUND);
		
    	SolarMeteoReadingDAO solarMeteoReadingDAO = null;
		List<SolarMeteoReadingTO> solarMeteoReadings = null;
		SolarMeteoReadingTO criteria = new SolarMeteoReadingTO();
        IntValue rowCount = new IntValue(0);
        List<SolarMeteoOcitanjeTO> solarMeteoOcitanjes = new ArrayList<SolarMeteoOcitanjeTO>();

        String filterExpression="where solar_meteo_reading.device_instance_id = "+stanicaId;
        String sort = "solar_meteo_reading.solar_meteo_reading_id DESC";
                
		try {           
			solarMeteoReadingDAO = OracleMWDAOFactory.getSolarMeteoReadingDAO();
			solarMeteoReadings = (List<SolarMeteoReadingTO>) (List) solarMeteoReadingDAO.select(
                    criteria, MessageDAO.SELECT_SQL_LIST,
                    filterExpression, sort, 0, 20,
                    rowCount);
			
			for(SolarMeteoReadingTO solarMeteoReadingTO:solarMeteoReadings){
				SolarMeteoOcitanjeTO ocitanje = ucitajOcitanjeIzReadinga(solarMeteoReadingTO);
				if(ocitanje!=null){
					ocitanje.setSolarMeteoStanicaId(stanicaId);
					return ocitanje;
				}
			}
            
        } catch (DAOException e) {
        	logger.error(null, e);
        	throw new WebApplicationException(e);
        }
		
		throw new WebApplicationException(Response.Status.NOT_FOUND);
    	
    }
    
    @POST
    @Consumes("application/json")
//	@ValidateRequest
	public SolarMeteoReadingTO create(@Valid SolarMeteoReadingTO newSolarMeteoReadingTO) {
    	SolarMeteoReadingDAO solarMeteoReadingDAO = null;
        Object primaryColVal = null;
    	logger.info("SolarMeteo Reading received, inserting:"+newSolarMeteoReadingTO.getSolarReading());
        try {
        	solarMeteoReadingDAO = OracleMWDAOFactory.getSolarMeteoReadingDAO();
        	primaryColVal = solarMeteoReadingDAO.create(newSolarMeteoReadingTO,
        			SolarMeteoReadingDAO.INSERT_SQL);

        	newSolarMeteoReadingTO.setSolarMeteoReadingId(
        			((Integer) primaryColVal).intValue());
        			
        } catch (DAOException e) {
        	logger.error("Error inserting data.", e);
        	
        	throw new WebApplicationException(e);
        }
        
		return newSolarMeteoReadingTO;
    }
    
    public SolarMeteoOcitanjeTO ucitajOcitanjeIzPoruke(MessageTO messageTO){
    	
    	int messageId =0;
    	try{
    		messageId= Integer.valueOf(messageTO.getMessageId());
    	} catch (NumberFormatException e){
    		
    	}
    	
    	try{
    	SolarMeteoOcitanjeTO reading = new SolarMeteoOcitanjeTO();
    	reading.setSolarMeteoOcitanjeId(messageId);
		String message = messageTO.getMessagePayload();
		Document document = HttpClient.loadXml("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+message, "UTF-8");
		NodeList nodeList = document.getElementsByTagName("value");
		if(nodeList.getLength()>0){
			Node node = nodeList.item(0);
			message=node.getTextContent();
			String[] splitString = message.split(",");
			
			if(splitString.length==15){
				reading.setPritisak(Double.valueOf(splitString[5]));
				reading.setVlaznost(Integer.valueOf(splitString[3].trim()));
				reading.setKolicinaPadavina(Double.valueOf(splitString[11]));
				DateFormat format = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
				Date date=null;
				try {
					date = format.parse(splitString[0].replace("\"", "")+" "+splitString[1].replace("\"", ""));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				reading.setDatum(date);
				reading.setTemperatura(Double.valueOf(splitString[2].trim()));
				reading.setSmjerVjetra(splitString[6]);
				reading.setBrzinaVjetra(Double.valueOf(splitString[7]));
				return reading;
			}
			
		}
    	} catch(Exception e){
    		return null;
    	}
    	return null;
	}
    
    public SolarMeteoOcitanjeTO ucitajOcitanjeIzReadinga(SolarMeteoReadingTO solarMeteoReadingTO){
    	try{
    		SolarMeteoOcitanjeTO reading = new SolarMeteoOcitanjeTO();
    		reading.setSolarMeteoOcitanjeId(solarMeteoReadingTO.getSolarMeteoReadingId());
    		String solarReading = solarMeteoReadingTO.getSolarReading();

    		String[] splitString = solarReading.split(",");

    		if(splitString.length==15){
    			reading.setPritisak(Double.valueOf(splitString[5]));
    			reading.setVlaznost(Integer.valueOf(splitString[3].trim()));
    			reading.setKolicinaPadavina(Double.valueOf(splitString[11]));
    			DateFormat format = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
    			Date date=null;
    			try {
    				date = format.parse(splitString[0].replace("\"", "")+" "+splitString[1].replace("\"", ""));
    			} catch (ParseException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    			reading.setDatum(date);
    			reading.setTemperatura(Double.valueOf(splitString[2].trim()));
    			reading.setSmjerVjetra(splitString[6]);
    			reading.setBrzinaVjetra(Double.valueOf(splitString[7]));
    			return reading;
    		}


    	} catch(Exception e){
    		return null;
    	}
    	return null;
    }

}
