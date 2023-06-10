package devs.mrp.springturkey.database.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "condition")
@Table(name = "TURKEY_CONDITION",
indexes = @Index(name = "condition_to_user_index", columnList = "user"))
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class Condition {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne
	@JoinColumn(name = "user", referencedColumnName = "id", nullable=false)
	@NotNull
	private User user; // sql script sets double foreign-key restriction to conditional group and target group

	@ManyToOne
	@JoinColumn(name = "conditional_group", referencedColumnName = "id", nullable = false)
	@NotNull
	private Group conditionalGroup;

	@ManyToOne
	@JoinColumn(name = "target_group", referencedColumnName = "id", nullable = false)
	@NotNull
	private Group targetGroup;

	@NotNull
	private Long requiredUsageMs;

	@NotNull
	private Integer lastDaysToConsider;

	@CreatedDate
	private LocalDateTime created;

	@LastModifiedDate
	private LocalDateTime edited;

	@Nullable
	private LocalDateTime deleted;

}
