package devs.mrp.springturkey.delta.validation.impl;

import java.util.Map;

import org.springframework.stereotype.Service;

import devs.mrp.springturkey.database.service.DeltaFacade;
import devs.mrp.springturkey.delta.validation.DataConstrainerTemplate;
import devs.mrp.springturkey.delta.validation.FieldValidator;

@Service("groupConstraints")
public class GroupDataConstrainer extends DataConstrainerTemplate {

	@Override
	protected Map<String, FieldValidator> getFieldMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected DeltaFacade getDeltaFacade() {
		// TODO Auto-generated method stub
		return null;
	}

}
