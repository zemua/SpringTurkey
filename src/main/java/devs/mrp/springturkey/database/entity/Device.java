package devs.mrp.springturkey.database.entity;

import java.util.Date;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import devs.mrp.springturkey.database.entity.enm.DeviceTypeEnum;
import devs.mrp.springturkey.database.entity.intf.UuidIdentifiedEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Document(collection = "device")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Device implements UuidIdentifiedEntity {

	@Id
	private UUID id;

	@DBRef
	@Indexed
	private User user;

	@NotNull
	private DeviceTypeEnum deviceType;

	@NotBlank
	private Long usageTime;

	@CreatedDate
	private Date created;

	@LastModifiedDate
	private Date edited;

	@Override
	public void setId(UUID id) {
		if (this.id != null) {
			throw new UnsupportedOperationException("ID is already defined");
		}
		this.id = id;
	}

}
