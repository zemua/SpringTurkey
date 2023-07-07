package devs.mrp.springturkey.controller.dto.exportentities;

import devs.mrp.springturkey.database.entity.Group;
import devs.mrp.springturkey.database.entity.enumerable.GroupType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class ExportGroupDto {

	private String name;
	private GroupType type;
	private Boolean preventClose;

	public static ExportGroupDto fromGroup(Group group) {
		ExportGroupDto dto = new ExportGroupDto();
		dto.name = group.getName();
		dto.type = group.getType();
		dto.preventClose = group.getPreventClose();
		return dto;
	}

}
