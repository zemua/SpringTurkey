package devs.mrp.springturkey.database.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import devs.mrp.springturkey.database.entity.enumerable.ActivityPlatform;
import devs.mrp.springturkey.database.entity.enumerable.CategoryType;
import devs.mrp.springturkey.validation.AlphaNumericSpaceConstraint;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

@Entity(name = "activity")
@Table(name = "turkey_activities",
indexes = @Index(name = "activity_to_user_index", columnList = "turkey_user"),
uniqueConstraints = { @UniqueConstraint(name = "uk__activity__name_and_type", columnNames = { "turkey_user", "activity_name", "activity_type" }) })
@EntityListeners(AuditingEntityListener.class)
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class Activity { // TODO test to find by id some created

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne
	@JoinColumn(name = "turkey_user", referencedColumnName = "id", nullable=false)
	@NotNull
	private TurkeyUser user;

	@Column(name = "activity_name")
	@NotBlank
	@AlphaNumericSpaceConstraint
	private String activityName;

	@Column(name = "activity_type")
	@Enumerated(EnumType.STRING)
	@NotNull
	private ActivityPlatform activityType;

	@Column(name = "category_type")
	@Enumerated(EnumType.STRING)
	@NotNull
	private CategoryType categoryType;

	@ManyToOne
	@JoinColumn(name = "turkey_group", referencedColumnName = "id", nullable = true)
	private Group group;

	@Nullable
	@Column(name = "prevent_closing")
	private Boolean preventClosing;

	@CreatedDate
	private LocalDateTime created;

	@LastModifiedDate
	private LocalDateTime edited;

	@Nullable
	private LocalDateTime deleted;

}
