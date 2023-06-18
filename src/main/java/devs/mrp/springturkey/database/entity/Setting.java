package devs.mrp.springturkey.database.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import devs.mrp.springturkey.database.entity.enumerable.PlatformType;
import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
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

@Entity(name = "setting")
@Table(name = "turkey_setting",
indexes = @Index(name = "setting_to_user_index", columnList = "turkey_user"),
uniqueConstraints = { @UniqueConstraint(name = "uk__platform__setting", columnNames = { "turkey_user", "settingKey", "platform" }) })
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class Setting {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne
	@JoinColumn(name = "turkey_user", referencedColumnName = "id", nullable=false)
	@NotNull
	private TurkeyUser user;

	@NotBlank
	private String settingKey;

	@Enumerated(EnumType.STRING)
	@NotBlank
	private PlatformType platform;

	@NotBlank
	private String settingValue;

	@CreatedDate
	private LocalDateTime created;

	@LastModifiedDate
	private LocalDateTime edited;

	@Nullable
	private LocalDateTime deleted;

}
