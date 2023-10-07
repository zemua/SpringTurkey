package devs.mrp.springturkey.delta.validation.entity;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

import devs.mrp.springturkey.database.entity.RandomQuestion;
import devs.mrp.springturkey.validation.AlphaNumericSpaceConstraint;
import devs.mrp.springturkey.validation.MaxBiggerThanMinConstraint;
import devs.mrp.springturkey.validation.MinTimeConstraint;
import devs.mrp.springturkey.validation.dtodef.MinMax;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@ToString
@MaxBiggerThanMinConstraint
public class RandomCheckCreationDelta implements MinMax<LocalTime> {

	@NotBlank
	@AlphaNumericSpaceConstraint
	private String name;

	@NotNull
	private LocalTime startActive;

	@NotNull
	private LocalTime endActive;

	@NotNull
	@MinTimeConstraint(minutes = 1)
	private LocalTime minCheckLapse;

	@NotNull
	private LocalTime maxCheckLapse;

	@NotNull
	@MinTimeConstraint(minutes = 1)
	private LocalTime reward;

	@NotNull
	private Set<DayOfWeek> activeDays;

	@NotNull
	private Set<RandomQuestion> negativeQuestions;

	@NotNull
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
