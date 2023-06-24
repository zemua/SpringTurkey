package devs.mrp.springturkey.database.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import devs.mrp.springturkey.database.entity.enumerable.ActivityType;
import devs.mrp.springturkey.database.entity.enumerable.CategoryType;
import jakarta.annotation.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "activity")
@Table(name = "turkey_activities",
indexes = @Index(name = "activity_to_user_index", columnList = "turkey_user"),
uniqueConstraints = { @UniqueConstraint(name = "uk__activity__name_and_type", columnNames = { "turkey_user", "activity_name", "activity_type" }) })
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class Activity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne
	@JoinColumn(name = "turkey_user", referencedColumnName = "id", nullable=false)
	@NotNull
	private TurkeyUser user;

	@Column(name = "activity_name")
	@NotBlank
	private String activityName;

	@Column(name = "activity_type")
	@Enumerated(EnumType.STRING)
	@NotNull
	private ActivityType activityType;

	@Column(name = "category_type")
	@Enumerated(EnumType.STRING)
	@NotNull
	private CategoryType categoryType;

	@ManyToOne
	@JoinColumn(name = "turkey_group", referencedColumnName = "id", nullable = true)
	private Group group;

	@OneToOne(mappedBy = "activity", cascade = CascadeType.ALL)
	@Setter
	private Uncloseable uncloseable;

	@CreatedDate
	private LocalDateTime created;

	@LastModifiedDate
	private LocalDateTime edited;

	@Nullable
	private LocalDateTime deleted;

}
