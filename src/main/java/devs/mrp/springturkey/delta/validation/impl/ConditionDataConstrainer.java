package devs.mrp.springturkey.delta.validation.impl;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
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
		return Map.of(
				"requiredUsageMs", FieldValidator.builder()
				.columnName("required_usage_ms")
				.predicate(StringUtils::isNumeric)
				.build(),
				"lastDaysToConsider", FieldValidator.builder()
				.columnName("last_days_to_consider")
				.predicate(StringUtils::isNumeric)
				.build(),
				"conditionalGroup", FieldValidator.builder()
				.columnName("conditional_group")
				.predicate(s -> getNamePattern().matcher(s).matches())
				.build()
				);
	}

	@Override
	protected DeltaFacadeService getDeltaFacadeService() {
		return deltaFacadeService;
	}

}
