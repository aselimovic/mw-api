package ba.qss.m2m.mw.api;

import javax.persistence.Column;

import ba.qss.framework.dataaccess.UserTO;

public class UserExTO extends UserTO {
	private Integer profileId;
	
	// getters
	public Integer getProfileId() { return profileId; }
	
	// setters
	public void setProfileId(Integer profileId) { this.profileId = profileId; }
	
	public UserExTO() { 
	}
}