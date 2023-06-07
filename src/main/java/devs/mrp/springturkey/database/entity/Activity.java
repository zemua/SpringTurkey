package devs.mrp.springturkey.database.entity;

import java.util.UUID;

import devs.mrp.springturkey.database.entity.enumerable.ActivityType;
import devs.mrp.springturkey.database.entity.enumerable.CategoryType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "categorizable")
@Table(name = "TURKEY_ACTIVITIES",
uniqueConstraints = { @UniqueConstraint(name = "uk__activity__name_and_type", columnNames = { "activityName", "activityType" }) },
indexes = @Index(name = "activity_to_group_index", columnList = "group"))
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class Activity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@NotBlank
	private String activityName;

	@NotNull
	private ActivityType activityType;

	@NotNull
	private CategoryType categoryType;

	@ManyToOne
	@JoinColumn(name = "id", nullable = true)
	private Group group;

}
