package devs.mrp.springturkey.database.entity;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import devs.mrp.springturkey.validation.MinDurationConstraint;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "condition")
@Table(name = "turkey_condition",
indexes = @Index(name = "condition_to_user_index", columnList = "turkey_user"))
@EntityListeners(AuditingEntityListener.class)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class Condition {

	@Id
	private UUID id;

	@ManyToOne
	@JoinColumn(name = "turkey_user", referencedColumnName = "id", nullable=false)
	@NotNull
	private TurkeyUser user; // sql script sets double foreign-key restriction to conditional group and target group

	@ManyToOne
	@JoinColumn(name = "conditional_group", referencedColumnName = "id", nullable = false)
	@NotNull
	private Group conditionalGroup;

	@ManyToOne
	@JoinColumn(name = "target_group", referencedColumnName = "id", nullable = false)
	@NotNull
	private Group targetGroup;

	@Column(name = "required_usage_ms")
	@NotNull
	@MinDurationConstraint(minutes = 1)
	private Duration requiredUsageMs;

	@Column(name = "last_days_to_consider")
	@NotNull
	@Min(0)
	private Integer lastDaysToConsider;

	@CreatedDate
	private LocalDateTime created;

	@LastModifiedDate
	private LocalDateTime edited;

	@Nullable
	private LocalDateTime deleted;

}
