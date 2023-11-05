package devs.mrp.springturkey.database.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.springturkey.components.LoginDetailsReader;
import devs.mrp.springturkey.database.entity.RandomCheck;
import devs.mrp.springturkey.database.repository.RandomCheckRepository;
import devs.mrp.springturkey.database.service.RandomCheckService;
import devs.mrp.springturkey.exceptions.AlreadyExistsException;
import devs.mrp.springturkey.utils.Duple;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class RandomCheckServiceImpl implements RandomCheckService {

	@Autowired
	private LoginDetailsReader loginDetailsReader;

	@Autowired
	private RandomCheckRepository randomCheckRepository;

	@Override
	public Flux<RandomCheck> findAllUserChecks() {
		return loginDetailsReader.getTurkeyUser().flatMapMany(user -> Flux.fromIterable(randomCheckRepository.findAllByUser(user)))
				.flatMap(check -> loginDetailsReader.isCurrentUser(check.getUser())
						.map(isCurrent -> new Duple<RandomCheck,Boolean>(check, isCurrent)))
				.filter(Duple::getValue2)
				.map(Duple::getValue1);
	}

	@Override
	public Mono<Integer> addNewCheck(RandomCheck check) {
		return insert(check);
	}

	public Mono<Integer> insert(RandomCheck check) {
		if (check.getId() == null) {
			return Mono.just(randomCheckRepository.save(check))
					.map(chk -> chk != null ? 1 : 0);
		} else {
			Optional<RandomCheck> existing = randomCheckRepository.findById(check.getId());
			if (existing.isPresent()) {
				return Mono.error(new AlreadyExistsException());
			}
			return Mono.just(randomCheckRepository.save(check))
					.map(chk -> chk != null ? 1 : 0);
		}
	}

}
