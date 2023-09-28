package devs.mrp.springturkey.delta.validation.constraints;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

import devs.mrp.springturkey.database.entity.RandomQuestion;
import devs.mrp.springturkey.validation.NullableAlphaNumericSpaceConstraint;
import devs.mrp.springturkey.validation.NullableBeforeAndAfterConstraint;
import devs.mrp.springturkey.validation.NullableMinTimeConstraint;
import devs.mrp.springturkey.validation.dtodef.MinMax;
import lombok.Getter;
import lombok.Setter;

@NullableBeforeAndAfterConstraint
@Getter
@Setter
public class RandomCheckModificationConstraints implements MinMax<LocalTime> {

	@NullableAlphaNumericSpaceConstraint
	private String name;

	private LocalTime startActive;

	private LocalTime endActive;

	@NullableMinTimeConstraint(minutes = 1)
	private LocalTime minCheckLapse;

	private LocalTime maxCheckLapse;

	@NullableMinTimeConstraint(minutes = 1)
	private LocalTime reward;

	private Set<DayOfWeek> activeDays;

	private Set<RandomQuestion> negativeQuestions;

	private Set<RandomQuestion> positiveQuestions;

	@Override
	public LocalTime minConstraint() {
		return minCheckLapse;
	}

	@Override
	public LocalTime maxConstraint() {
		return maxCheckLapse;
	}

}
