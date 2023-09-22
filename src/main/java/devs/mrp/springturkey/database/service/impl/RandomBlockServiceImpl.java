package devs.mrp.springturkey.database.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import devs.mrp.springturkey.Exceptions.AlreadyExistsException;
import devs.mrp.springturkey.Exceptions.DoesNotBelongToUserException;
import devs.mrp.springturkey.components.LoginDetailsReader;
import devs.mrp.springturkey.database.entity.RandomBlock;
import devs.mrp.springturkey.database.repository.RandomBlockRepository;
import devs.mrp.springturkey.database.service.RandomBlockService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class RandomBlockServiceImpl implements RandomBlockService {

	@Autowired
	private LoginDetailsReader loginDetailsReader;

	@Autowired
	private RandomBlockRepository randomBlockRepository;

	@Override
	public Flux<RandomBlock> findAllUserBlocks() {
		return Flux.fromIterable(randomBlockRepository.findAllByUser(loginDetailsReader.getTurkeyUser()))
				.filter(block -> loginDetailsReader.isCurrentUser(block.getUser()));
	}

	@Override
	public Mono<Integer> addNewBlock(RandomBlock block) {
		if (!loginDetailsReader.isCurrentUser(block.getUser())) {
			return Mono.error(new DoesNotBelongToUserException());
		}
		try {
			return insert(block);
		} catch (DataIntegrityViolationException e) {
			return Mono.error(new AlreadyExistsException());
		}
	}

	public Mono<Integer> insert(RandomBlock block) {
		if (block.getId() == null) {
			return Mono.just(randomBlockRepository.save(block))
					.map(blk -> blk != null ? 1 : 0);
		} else {
			return Mono.just(randomBlockRepository.insert(block.getId(), block.getUser().getId(), block.getType().name(), block.getName(), block.getQuestion(), block.getFrequency(), block.getMultiplier()));
		}
	}

}
