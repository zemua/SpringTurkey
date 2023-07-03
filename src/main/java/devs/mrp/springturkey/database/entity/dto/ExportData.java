package devs.mrp.springturkey.database.entity.dto;

import java.util.List;

import devs.mrp.springturkey.database.entity.Activity;
import devs.mrp.springturkey.database.entity.Condition;
import devs.mrp.springturkey.database.entity.Device;
import devs.mrp.springturkey.database.entity.Group;
import devs.mrp.springturkey.database.entity.Setting;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExportData {

	private List<Device> otherDevices;
	private List<Activity> activities;
	private List<Group> groups;
	private List<Condition> conditions;
	private List<Setting> settings;

}
