package devs.mrp.springturkey.database.entity;

import java.util.Date;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import devs.mrp.springturkey.database.entity.enumerable.DeviceType;
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

@Entity(name = "device")
@Table(name = "TURKEY_DEVICE",
indexes = @Index(name = "device_to_user_index", columnList = "user"))
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Device {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne
	@JoinColumn(name = "id", nullable=false)
	@NotNull
	private User user;

	@NotNull
	private DeviceType deviceType;

	@NotNull
	private Long usageTime;

	@CreatedDate
	private Date created;

	@LastModifiedDate
	private Date edited;

}
