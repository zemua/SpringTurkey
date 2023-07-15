package devs.mrp.springturkey.delta.validation.impl;

import java.util.Map;

import org.springframework.stereotype.Service;

import devs.mrp.springturkey.database.service.DeltaFacadeService;
import devs.mrp.springturkey.delta.DeltaTable;
import devs.mrp.springturkey.delta.validation.DataConstrainerTemplate;
import devs.mrp.springturkey.delta.validation.FieldValidator;

@Service("settingConstraints")
public class SettingDataConstrainer extends DataConstrainerTemplate {

	@Override
	protected boolean isValidTable(DeltaTable table) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected Map<String, FieldValidator> getFieldMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected DeltaFacadeService getDeltaFacadeService() {
		// TODO Auto-generated method stub
		return null;
	}

}
