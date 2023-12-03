package devs.mrp.springturkey.controller.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode
public class PushDeltaResponseDto {

	private UUID uuid;
	private boolean success;

}
