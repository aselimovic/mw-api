package ba.qss.m2m.mw.api;

import ba.qss.framework.dataaccess.UserTO;

public class UserExTO extends UserTO {
	private int profileId;
	
	// getters
	public int getProfileId() { return profileId; }
	
	// setters
	public void setProfileId(int profileId) { this.profileId = profileId; }
	
	public UserExTO() { 
		 super();
	}
}