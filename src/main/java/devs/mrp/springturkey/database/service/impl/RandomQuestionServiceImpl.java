package devs.mrp.springturkey.database.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import devs.mrp.springturkey.Exceptions.AlreadyExistsException;
import devs.mrp.springturkey.Exceptions.DoesNotBelongToUserException;
import devs.mrp.springturkey.components.LoginDetailsReader;
import devs.mrp.springturkey.database.entity.RandomQuestion;
import devs.mrp.springturkey.database.repository.RandomQuestionRepository;
import devs.mrp.springturkey.database.service.RandomQuestionService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class RandomQuestionServiceImpl implements RandomQuestionService {

	@Autowired
	private LoginDetailsReader loginDetailsReader;

	@Autowired
	private RandomQuestionRepository randomBlockRepository;

	@Override
	public Flux<RandomQuestion> findAllUserQuestions() {
		return Flux.fromIterable(randomBlockRepository.findAllByUser(loginDetailsReader.getTurkeyUser()))
				.filter(block -> loginDetailsReader.isCurrentUser(block.getUser()));
	}

	@Override
	public Mono<Integer> addNewQuestion(RandomQuestion question) {
		if (!loginDetailsReader.isCurrentUser(question.getUser())) {
			return Mono.error(new DoesNotBelongToUserException());
		}
		try {
			return insert(question);
		} catch (DataIntegrityViolationException e) {
			return Mono.error(new AlreadyExistsException());
		}
	}

	public Mono<Integer> insert(RandomQuestion question) {
		if (question.getId() == null) {
			return Mono.just(randomBlockRepository.save(question))
					.map(blk -> blk != null ? 1 : 0);
		} else {
			return Mono.just(randomBlockRepository.insert(question.getId(), question.getUser().getId(), question.getType().name(), question.getName(), question.getQuestion(), question.getFrequency(), question.getMultiplier()));
		}
	}

}
