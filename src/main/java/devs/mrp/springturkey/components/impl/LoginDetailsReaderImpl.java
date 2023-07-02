package devs.mrp.springturkey.components.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import devs.mrp.springturkey.components.LoginDetailsReader;
import devs.mrp.springturkey.database.entity.TurkeyUser;
import devs.mrp.springturkey.database.repository.UserRepository;

@Component
public class LoginDetailsReaderImpl implements LoginDetailsReader {

	@Autowired
	private UserRepository userRepository;

	private static final Object CREATE_USER_LOCK = new Object();

	@Override
	public String getUsername() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

	@Override
	public boolean isCurrentUser(TurkeyUser user) {
		return user.getEmail().equals(getUsername());
	}

	@Override
	public TurkeyUser getTurkeyUser() {
		return userRepository.findByEmail(getUsername());
	}

	@Override
	public TurkeyUser setupCurrentUser() {
		TurkeyUser currentUser = getTurkeyUser();
		if (currentUser == null) {
			synchronized (CREATE_USER_LOCK) {
				if (getTurkeyUser() == null) {
					currentUser = userRepository.save(TurkeyUser.builder().email(getUsername()).build());
				}
			}
		}
		return currentUser;
	}

}
