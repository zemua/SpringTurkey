package devs.mrp.springturkey.database.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.springturkey.components.LoginDetailsReader;
import devs.mrp.springturkey.database.entity.RandomCheck;
import devs.mrp.springturkey.database.repository.RandomCheckRepository;
import devs.mrp.springturkey.database.service.RandomCheckService;
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
		return Flux.fromIterable(randomCheckRepository.findAllByUser(loginDetailsReader.getTurkeyUser()))
				.filter(check -> loginDetailsReader.isCurrentUser(check.getUser()));
	}

	@Override
	public Mono<Integer> addNewCheck(RandomCheck check) {
		// TODO Auto-generated method stub
		return Mono.just(1);
	}

	public Mono<Integer> insert(RandomCheck check) {
		if (check.getId() == null) {
			return Mono.just(randomCheckRepository.save(check))
					.map(chk -> chk != null ? 1 : 0);
		} else {
			// TODO check how to do insert without update and don't mess up the many-to-many
			return Mono.just(randomCheckRepository.insert(check.getId(), check.getUser(), check.getName(), check.getStartActive(), check.getEndActive(), check.getMinCheckLapse(), check.getMaxCheckLapse(), check.getReward(), check.getActiveDays()));
		}
	}

}
