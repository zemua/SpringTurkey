package devs.mrp.springturkey.database.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.springturkey.components.LoginDetailsReader;
import devs.mrp.springturkey.database.entity.RandomBlock;
import devs.mrp.springturkey.database.repository.RandomBlockRepository;
import devs.mrp.springturkey.database.service.RandomBlockService;
import reactor.core.publisher.Flux;

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

}
