package devs.mrp.springturkey.database.entity;

import java.util.UUID;

import devs.mrp.springturkey.database.entity.enumerable.PlatformType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "setting")
@Table(name = "TURKEY_SETTING",
indexes = @Index(name = "setting_index", columnList = "settingKey, platform"))
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class Setting {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@NotBlank
	private String settingKey;

	@NotBlank
	private PlatformType platform;

	@NotBlank
	private String settingValue;

}
