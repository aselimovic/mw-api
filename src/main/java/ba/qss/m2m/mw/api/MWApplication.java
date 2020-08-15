package ba.qss.m2m.mw.api;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.jboss.resteasy.plugins.interceptors.CorsFilter;

/**
 * 
 * @author adnan
 */
// Java Servlet 3.0 Final Release Specification - 12.2 Specification of Mappings
// http://download.oracle.com/otn-pub/jcp/servlet-3.0-fr-eval-oth-JSpec/servlet-3_0-final-spec.pdf
//
// The empty string ("") is a special URL pattern that exactly maps to the
// application's context root.
@ApplicationPath("") // The value of the application path may be overridden using a servlet-mapping element in the web.xml.
public class MWApplication extends Application {

//	private static final Set<Object> singletons = Collections.emptySet();

	private Set<Object> singletons = new HashSet<Object>();	
	
	public MWApplication() {
		// How to enable the CORS Filter in RESTEasy &#8211; CodepediaOrg
		// https://www.codepedia.org/ama/how-to-enable-cors-filter-in-resteasy
        CorsFilter corsFilter = new CorsFilter();
        corsFilter.setAllowedHeaders("accept, content-type, origin");
        corsFilter.getAllowedOrigins().add("*");
        corsFilter.setAllowedMethods("OPTIONS, GET, POST, DELETE, PUT, PATCH");
        singletons.add(corsFilter);		
	}
	
	/**
	 * The getClasses() method allocates a HashSet, populates it with @Path
	 * annotated classes, and returns the set.
	 * 
	 * @return a list of JAX-RS web service and provider classes
	 */
	// The @Override annotation will prevent you from accidentally overloading
	// when you don't mean to. (TIJ4 p. 256)
	@Override
	public Set<Class<?>> getClasses() { // In Java SE5, Class<?> is preferred over plain Class (TIJ4 p. 566)
		HashSet<Class<?>> result = new HashSet<Class<?>>();
		
		/*
		 * RESTful Java with JAX-RS 2.0, Second Edition (O'Reilly Media) -
		 * CHAPTER 14. Deployment and Integration
		 */

		result.add(BindingEntityResource.class);
		result.add(IPGatewayResource.class);
		result.add(MessageTypeResource.class);
		result.add(PartnerResource.class);
		result.add(ProtocolTypeResource.class);
		result.add(RoleResource.class);
		result.add(RoutingTableResource.class);
		result.add(ReportingPeriodResource.class);
		result.add(UserResource.class);
		result.add(UserRoleResource.class);
		result.add(XmlSchemaResource.class);
		result.add(XsltStylesheetResource.class);
		result.add(PartyResource.class);
		result.add(PartyTypeResource.class);
		result.add(PartyRoleTypeResource.class);
		result.add(PartyRoleResource.class);
		result.add(PartyRelationshipTypeResource.class);
		result.add(PartyRelationshipResource.class);
		result.add(PartnerHierarchyResource.class);
		result.add(GeographicBoundaryResource.class);
		result.add(DeviceResource.class);
		result.add(DeviceTypeResource.class);
		result.add(DeviceInstanceStatusTypeResource.class);
		result.add(DeviceInstanceStatusResource.class);
		result.add(DeviceInstanceResource.class);
		result.add(SimCardResource.class);
		result.add(SimCardStatusTypeResource.class);
		result.add(SimCardStatusResource.class);
		result.add(PartyUserAssociationResource.class);
		result.add(PhysicalEnvTypeResource.class);
		result.add(PhysicalEnvResource.class);
		result.add(ProfileResource.class);
		result.add(ProfileMsisdnResource.class);
		result.add(PotrosacResource.class);
		result.add(PotrosnjaResource.class);
		result.add(PostavkeIzvjestajaResource.class);
		result.add(KategorijaResource.class);
		result.add(MessageResource.class);
		result.add(LiferayUserResource.class);
		result.add(ProfileUserResource.class);
		result.add(SolarMeteoResource.class);
		// register exception mappers
		result.add(Node.class);
		result.add(DAOExceptionMapper.class);
		result.add(MethodConstraintViolationExceptionMapper.class);
		
		return result;
	}
	
	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}
}
