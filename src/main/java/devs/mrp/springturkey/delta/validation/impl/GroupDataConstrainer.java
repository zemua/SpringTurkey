package devs.mrp.springturkey.delta.validation.impl;

import static java.util.Map.entry;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.springturkey.database.service.DeltaFacadeService;
import devs.mrp.springturkey.delta.DeltaTable;
import devs.mrp.springturkey.delta.validation.DataConstrainerTemplate;
import devs.mrp.springturkey.delta.validation.FieldValidator;

@Service("groupConstraints")
public class GroupDataConstrainer extends DataConstrainerTemplate {

	@Autowired
	private DeltaFacadeService deltaFacadeService;

	@Override
	protected Map<String, FieldValidator> getFieldMap() {
		return Map.ofEntries(
				entry("name", FieldValidator.builder()
						.columnName("name")
						.predicate(s -> getNamePattern().matcher(s).matches())
						.build()),
				entry("preventClose", FieldValidator.builder()
						.columnName("prevent_close")
						.predicate(s -> getBooleanPattern().matcher(s).matches())
						.build())
				);
	}

	@Override
	protected DeltaFacadeService getDeltaFacadeService() {
		return deltaFacadeService;
	}

	@Override
	protected boolean isValidTable(DeltaTable table) {
		return DeltaTable.GROUP.equals(table);
	}

}
