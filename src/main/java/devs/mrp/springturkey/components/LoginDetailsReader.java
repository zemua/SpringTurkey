package devs.mrp.springturkey.components;

import devs.mrp.springturkey.database.entity.TurkeyUser;

public interface LoginDetailsReader {

	public String getUsername();

	public boolean isCurrentUser(TurkeyUser user);

	public TurkeyUser getTurkeyUser();

}
