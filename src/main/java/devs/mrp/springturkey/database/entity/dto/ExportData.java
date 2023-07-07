package devs.mrp.springturkey.database.entity.dto;

import java.util.List;

import devs.mrp.springturkey.database.entity.Activity;
import devs.mrp.springturkey.database.entity.Condition;
import devs.mrp.springturkey.database.entity.Device;
import devs.mrp.springturkey.database.entity.Group;
import devs.mrp.springturkey.database.entity.Setting;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;

@Getter
@Setter
@With
@AllArgsConstructor
@NoArgsConstructor
public class ExportData {

	private List<Device> otherDevices;
	private List<Activity> activities;
	private List<Group> groups;
	private List<Condition> conditions;
	private List<Setting> settings;

}
