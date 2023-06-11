package devs.mrp.springturkey.components.impl;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import devs.mrp.springturkey.components.LoginDetailsReader;
import devs.mrp.springturkey.database.entity.User;

@Component
public class LoginDetailsReaderImpl implements LoginDetailsReader {

	@Override
	public String getUsername() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

	@Override
	public boolean isCurrentUser(User user) {
		return user.getEmail().equals(getUsername());
	}

}
