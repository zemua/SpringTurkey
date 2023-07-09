package devs.mrp.springturkey.delta.validation.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import devs.mrp.springturkey.Exceptions.WrongDataException;
import devs.mrp.springturkey.delta.validation.Table;
import devs.mrp.springturkey.delta.validation.DataConstrainer;
import devs.mrp.springturkey.delta.validation.DataConstrainerProvider;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DataConstrainerProviderImpl implements DataConstrainerProvider {

	@Autowired
	@Qualifier("deviceConstraints")
	private DataConstrainer deviceConstraints;

	@Autowired
	@Qualifier("groupConstraints")
	private DataConstrainer groupConstraints;

	@Autowired
	@Qualifier("activityConstraints")
	private DataConstrainer activityConstraints;

	@Autowired
	@Qualifier("conditionConstraints")
	private DataConstrainer conditionConstraints;

	@Autowired
	@Qualifier("settingConstraints")
	private DataConstrainer settingConstraints;


	@Override
	public DataConstrainer getFor(Table table) throws WrongDataException {
		if (table == null) {
			throw new WrongDataException("Invalid table");
		}
		switch (table) {
		case DEVICE:
			return deviceConstraints;
		case GROUP:
			return groupConstraints;
		case ACTIVITY:
			return activityConstraints;
		case CONDITION:
			return conditionConstraints;
		case SETTING:
			return settingConstraints;
		default:
			log.error("Enum case {} not contemplated", table);
			throw new RuntimeException("Enum case not contemplated");
		}
	}

}
