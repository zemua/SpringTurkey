package devs.mrp.springturkey.components.impl;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;

import com.nimbusds.oauth2.sdk.util.StringUtils;

import devs.mrp.springturkey.Exceptions.TurkeySurpriseException;
import devs.mrp.springturkey.components.LoginDetailsReader;
import devs.mrp.springturkey.database.entity.TurkeyUser;
import devs.mrp.springturkey.database.repository.UserRepository;
import reactor.core.publisher.Mono;

@Component
public class LoginDetailsReaderImpl implements LoginDetailsReader {

	@Autowired
	private UserRepository userRepository;

	private static final Object CREATE_USER_LOCK = new Object();

	@Override
	public Mono<String> getUserId() { // TODO fix tests
		return ReactiveSecurityContextHolder.getContext().map(context -> {

			Authentication auth = context.getAuthentication();

			if (Objects.isNull(auth)) {
				throw new TurkeySurpriseException("Authentication context is missing");
			}
			String id = auth.getName();

			if (StringUtils.isBlank(id)) {
				throw new TurkeySurpriseException("No user id found in Auth context");
			}
			return id;
		});
	}

	@Override
	public boolean isCurrentUser(TurkeyUser user) {
		return user.getEmail().equals(getUserId());
	}

	@Override
	public Mono<TurkeyUser> getTurkeyUser() { // TODO it could be not found in the db
		return getUserId().map(userRepository::findByEmail);
	}

	@Override
	public Mono<TurkeyUser> setupCurrentUser() {
		return getTurkeyUser().flatMap(currentUser -> {
			if (currentUser == null) {
				return getUserId().map(email -> userRepository.save(TurkeyUser.builder().email(email).build()));
			}
			return Mono.just(currentUser);
		});
	}

}
