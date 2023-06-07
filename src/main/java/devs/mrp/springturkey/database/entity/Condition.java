package devs.mrp.springturkey.database.entity;

import java.util.UUID;

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
indexes = {
		@Index(name = "condition_to_conditionalgroup_index", columnList = "conditionalGroup"),
		@Index(name = "condition_to_targetgroup_index", columnList = "targetGroup")
})
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
	@JoinColumn(name = "id", nullable = false)
	@NotNull
	private Group conditionalGroup;

	@ManyToOne
	@JoinColumn(name = "id", nullable = false)
	@NotNull
	private Group targetGroup;

	@NotNull
	private Long requiredUsageMs;

	@NotNull
	private Integer lastDaysToConsider;

}
