package devs.mrp.springturkey.database.entity;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Document(collection = "user")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class User {

	@Id
	private UUID id;

	@NotBlank
	@Indexed(unique = true)
	private String email;

	public void setId(UUID id) {
		if (this.id != null) {
			throw new UnsupportedOperationException("ID is already defined");
		}
		this.id = id;
	}

}
