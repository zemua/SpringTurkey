package devs.mrp.springturkey.database.controller.dto;

import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserMailDto {

	@Email
	private String email;

}
