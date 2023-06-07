package devs.mrp.springturkey.database.entity;

import java.util.UUID;

import devs.mrp.springturkey.database.entity.enumerable.DeviceType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "uncloseable")
@Table(name = "TURKEY_UNCLOSEABLE",
indexes = @Index(name = "uncloseable_index", columnList = "processName, deviceType"))
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class Uncloseable {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@NotBlank
	private String processName;

	@NotNull
	private DeviceType deviceType;

}
