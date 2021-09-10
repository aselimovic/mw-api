package ba.qss.m2m.mw.api;

import javax.persistence.Column;

import ba.qss.framework.dataaccess.UserTO;

public class UserExTO extends UserTO {
	@Column(name="profile_id", insertable=false, updatable=false)	
	private int profileId;
	
	// getters
	public int getProfileId() { return profileId; }
	
	// setters
	public void setProfileId(int profileId) { this.profileId = profileId; }
	
	public UserExTO() { 
		 super();
	}
}