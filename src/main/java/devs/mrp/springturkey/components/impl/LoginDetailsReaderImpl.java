package devs.mrp.springturkey.components.impl;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import devs.mrp.springturkey.components.LoginDetailsReader;
import devs.mrp.springturkey.database.entity.TurkeyUser;

@Component
public class LoginDetailsReaderImpl implements LoginDetailsReader {

	@Override
	public String getUsername() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

	@Override
	public boolean isCurrentUser(TurkeyUser user) {
		return user.getEmail().equals(getUsername());
	}

}
