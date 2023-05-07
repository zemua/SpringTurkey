package devs.mrp.springturkey.database.entity;

import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Document(collection = "device")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Device {

	@Id
	private String id;

	@DBRef
	@Indexed
	private User user;

	@NotBlank
	private Long usageTime;

	@CreatedDate
	private Date created;

	@LastModifiedDate
	private Date edited;

}
