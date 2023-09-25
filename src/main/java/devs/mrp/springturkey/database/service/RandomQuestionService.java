package devs.mrp.springturkey.database.service;

import devs.mrp.springturkey.database.entity.RandomQuestion;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RandomQuestionService {

	public Flux<RandomQuestion> findAllUserQuestions();

	public Mono<Integer> addNewQuestion(RandomQuestion block);

}
