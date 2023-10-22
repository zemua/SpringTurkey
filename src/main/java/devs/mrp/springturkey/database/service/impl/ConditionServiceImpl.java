package devs.mrp.springturkey.database.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import devs.mrp.springturkey.Exceptions.AlreadyExistsException;
import devs.mrp.springturkey.Exceptions.DoesNotBelongToUserException;
import devs.mrp.springturkey.Exceptions.WrongDataException;
import devs.mrp.springturkey.components.LoginDetailsReader;
import devs.mrp.springturkey.database.entity.Condition;
import devs.mrp.springturkey.database.repository.ConditionRepository;
import devs.mrp.springturkey.database.service.ConditionService;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class ConditionServiceImpl implements ConditionService {

	@Autowired
	private LoginDetailsReader loginDetailsReader;

	@Autowired
	private ConditionRepository conditionRepository;

	@Override
	public Flux<Condition> findAllUserConditions() {
		return loginDetailsReader.getTurkeyUser().flatMapMany(user -> Flux.fromIterable(conditionRepository.findAllByUser(user)))
				.filter(condition -> loginDetailsReader.isCurrentUser(condition.getUser()));
	}

	@Override
	public Mono<Integer> addNewCondition(Condition condition) {
		if (!isAllCurrentUser(condition)) {
			log.error("Condition does not belong to user");
			log.debug(condition.toString());
			return Mono.error(new DoesNotBelongToUserException());
		}
		if (isTargetSameAsConditional(condition)) {
			log.error("Conditional group and target group are the same");
			log.debug(condition.toString());
			return Mono.error(new WrongDataException("Same groups on condition"));
		}
		try {
			return insert(condition);
		} catch (DataIntegrityViolationException e) {
			log.error("Error inserting condition", e);
			return Mono.error(new AlreadyExistsException());
		}
	}

	private boolean isAllCurrentUser(Condition condition) {
		return loginDetailsReader.isCurrentUser(condition.getUser())
				&& loginDetailsReader.isCurrentUser(condition.getConditionalGroup().getUser())
				&& loginDetailsReader.isCurrentUser(condition.getTargetGroup().getUser());
	}

	private boolean isTargetSameAsConditional(Condition condition) {
		return condition.getConditionalGroup().getId().equals(condition.getTargetGroup().getId());
	}

	private Mono<Integer> insert(Condition condition) {
		if (condition.getId() == null) {
			return Mono.just(conditionRepository.save(condition))
					.map(cond -> cond != null ? 1 : 0);
		} else {
			return Mono.just(conditionRepository.insert(
					condition.getId(),
					condition.getUser().getId(),
					condition.getConditionalGroup().getId(),
					condition.getTargetGroup().getId(),
					condition.getRequiredUsageMs(),
					condition.getLastDaysToConsider()
					));
		}
	}

}
