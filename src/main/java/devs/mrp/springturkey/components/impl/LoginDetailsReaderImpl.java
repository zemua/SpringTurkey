package devs.mrp.springturkey.components.impl;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;

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
	public Mono<String> getUserId() {
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
	public Mono<Boolean> isCurrentUser(TurkeyUser user) {
		return getUserId().map(id -> StringUtils.equals(id, user.getEmail()));
	}

	@Override
	public Mono<TurkeyUser> getTurkeyUser() {
		return getUserId()
				.map(userRepository::findByEmail)
				.flatMap(optional -> optional.isPresent() ? Mono.just(optional.get()) : Mono.empty());
	}

	@Override
	public Mono<TurkeyUser> setupCurrentUser() {
		return getTurkeyUser()
				.switchIfEmpty(Mono.defer(() -> {
					return getUserId()
							.map(id -> userRepository.save(TurkeyUser.builder().email(id).build()));
				}));
	}

}
