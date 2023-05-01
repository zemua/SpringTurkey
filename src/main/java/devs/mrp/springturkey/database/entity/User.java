package devs.mrp.springturkey.database.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;

@Document(collection = "user")
public class User {

	@Id
	private String id;

	@NotBlank
	@Indexed
	private String email;

}
