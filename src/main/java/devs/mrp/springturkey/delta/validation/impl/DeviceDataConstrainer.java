package devs.mrp.springturkey.delta.validation.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.springturkey.database.service.DeltaFacadeService;
import devs.mrp.springturkey.delta.DeltaTable;
import devs.mrp.springturkey.delta.validation.DataConstrainerTemplate;
import devs.mrp.springturkey.delta.validation.FieldValidator;

@Service("deviceConstraints")
public class DeviceDataConstrainer extends DataConstrainerTemplate {

	@Autowired
	private DeltaFacadeService deltaFacadeService;

	@Override
	protected boolean isValidTable(DeltaTable table) {
		return false;
	}

	@Override
	protected Map<String, FieldValidator> getFieldMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected DeltaFacadeService getDeltaFacadeService() {
		return deltaFacadeService;
	}

}
