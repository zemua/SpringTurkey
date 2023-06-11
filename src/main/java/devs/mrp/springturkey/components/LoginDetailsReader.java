package devs.mrp.springturkey.components;

import devs.mrp.springturkey.database.entity.User;

public interface LoginDetailsReader {

	public String getUsername();

	public boolean isCurrentUser(User user);

}
