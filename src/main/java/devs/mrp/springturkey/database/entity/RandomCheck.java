package devs.mrp.springturkey.database.entity;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.uuid.Generators;

import devs.mrp.springturkey.validation.AlphaNumericSpaceConstraint;
import devs.mrp.springturkey.validation.MaxBiggerThanMinConstraint;
import devs.mrp.springturkey.validation.MinTimeConstraint;
import devs.mrp.springturkey.validation.dtodef.MinMax;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "randomcheck")
@Table(name = "random_check",
indexes = {
		@Index(name = "check_to_user_index", columnList = "turkey_user")})
@EntityListeners(AuditingEntityListener.class)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@MaxBiggerThanMinConstraint
public class RandomCheck implements MinMax<LocalTime> {

	@Id
	@Column(name = "id")
	private UUID id;

	@ManyToOne
	@JoinColumn(name = "turkey_user", referencedColumnName = "id", nullable=false)
	@NotNull
	private TurkeyUser user;

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

	private Set<DayOfWeek> activeDays;

	@ManyToMany
	@JoinTable(
			name = "negative_controls",
			joinColumns = @JoinColumn(name = "check_id"),
			inverseJoinColumns = @JoinColumn(name = "question_id"))
	private Set<RandomQuestion> negativeQuestions;

	@ManyToMany
	@JoinTable(
			name = "positive_controls",
			joinColumns = @JoinColumn(name = "check_id"),
			inverseJoinColumns = @JoinColumn(name = "question_id"))
	private Set<RandomQuestion> positiveQuestions;

	@CreatedDate
	private LocalDateTime created;

	@LastModifiedDate
	private LocalDateTime edited;

	@Nullable
	private LocalDateTime deleted;

	@PrePersist
	protected void onCreate() {
		if (Objects.isNull(this.id)) {
			this.id = Generators.timeBasedGenerator().generate();
		}
	}

	@Override
	public LocalTime minConstraint() {
		return minCheckLapse;
	}

	@Override
	public LocalTime maxConstraint() {
		return maxCheckLapse;
	}

}
