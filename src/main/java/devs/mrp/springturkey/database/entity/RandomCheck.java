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
@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class RandomCheck {

	// TODO all entities to keep uuid if provided when saving

	@Id
	@Column(name = "id")
	private UUID id;

	@ManyToOne
	@JoinColumn(name = "turkey_user", referencedColumnName = "id", nullable=false)
	@NotNull
	private TurkeyUser user;

	@NotNull
	private String name;

	@NotNull
	private LocalTime startActive;

	@NotNull
	private LocalTime endActive;

	@NotNull
	// TODO validate > 00:00
	private LocalTime minCheckLapse;

	@NotNull
	// TODO validate >= minCheckLapse
	private LocalTime maxCheckLapse;

	@NotNull
	// TODO validate > 00:00
	private LocalTime reward;

	private Set<DayOfWeek> activeDays;

	@ManyToMany
	@JoinTable(
			name = "negative_controls",
			joinColumns = @JoinColumn(name = "check_id"),
			inverseJoinColumns = @JoinColumn(name = "block_id"))
	private Set<RandomBlock> negativeControls;

	@ManyToMany
	@JoinTable(
			name = "positive_controls",
			joinColumns = @JoinColumn(name = "check_id"),
			inverseJoinColumns = @JoinColumn(name = "block_id"))
	private Set<RandomBlock> positiveControls;

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

}
