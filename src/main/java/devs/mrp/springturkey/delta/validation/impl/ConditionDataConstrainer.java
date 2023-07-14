package devs.mrp.springturkey.delta.validation.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.springturkey.database.service.DeltaFacadeService;
import devs.mrp.springturkey.delta.DeltaTable;
import devs.mrp.springturkey.delta.validation.DataConstrainerTemplate;
import devs.mrp.springturkey.delta.validation.FieldValidator;

@Service("conditionConstraints")
public class ConditionDataConstrainer extends DataConstrainerTemplate {

	@Autowired
	private DeltaFacadeService deltaFacadeService;

	@Override
	protected boolean isValidTable(DeltaTable table) {
		return DeltaTable.CONDITION.equals(table);
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
