package devs.mrp.springturkey.database.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import devs.mrp.springturkey.database.entity.enumerable.GroupType;
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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "group")
@Table(name = "turkey_group",
indexes = {
		@Index(name = "group_to_user_index", columnList = "turkey_user"),
		@Index(name = "group_and_user_index", columnList = "id, turkey_user") })
@EntityListeners(AuditingEntityListener.class)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class Group {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id")
	private UUID id;

	@ManyToOne
	@JoinColumn(name = "turkey_user", referencedColumnName = "id", nullable=false)
	@NotNull
	private TurkeyUser user;

	@NotBlank
	@AlphaNumericSpaceConstraint
	private String name;

	@Enumerated(EnumType.STRING)
	@NotNull
	private GroupType type;

	@Nullable
	@Column(name = "prevent_close")
	private Boolean preventClose;

	@CreatedDate
	private LocalDateTime created;

	@LastModifiedDate
	private LocalDateTime edited;

	@Nullable
	private LocalDateTime deleted;

}
