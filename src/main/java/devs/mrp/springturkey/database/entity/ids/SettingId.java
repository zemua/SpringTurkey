package devs.mrp.springturkey.database.entity.ids;

import java.io.Serializable;

import devs.mrp.springturkey.database.entity.TurkeyUser;
import devs.mrp.springturkey.database.entity.enumerable.PlatformType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
public class SettingId implements Serializable {

	@ManyToOne
	@JoinColumn(name = "turkey_user", referencedColumnName = "id", nullable=false)
	@NotNull
	private TurkeyUser user;

	@Enumerated(EnumType.STRING)
	@NotBlank
	private PlatformType platform;

	@Column(name = "setting_key")
	@NotBlank
	private String settingKey;

}
