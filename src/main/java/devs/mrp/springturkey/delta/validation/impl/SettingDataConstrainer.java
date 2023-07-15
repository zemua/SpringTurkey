package devs.mrp.springturkey.delta.validation.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.springturkey.database.service.DeltaFacadeService;
import devs.mrp.springturkey.delta.DeltaTable;
import devs.mrp.springturkey.delta.validation.DataConstrainerTemplate;
import devs.mrp.springturkey.delta.validation.FieldValidator;

@Service("settingConstraints")
public class SettingDataConstrainer extends DataConstrainerTemplate {

	@Autowired
	private DeltaFacadeService deltaFacadeService;

	@Override
	protected boolean isValidTable(DeltaTable table) {
		return DeltaTable.SETTING.equals(table);
	}

	@Override
	protected Map<String, FieldValidator> getFieldMap() {
		return Map.of(
				"settingValue", FieldValidator.builder()
				.columnName("setting_value")
				.predicate(s -> getNamePattern().matcher(s).matches())
				.build()
				);
	}

	@Override
	protected DeltaFacadeService getDeltaFacadeService() {
		return deltaFacadeService;
	}

}
