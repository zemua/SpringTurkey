package devs.mrp.springturkey.delta.validation.impl;

import static java.util.Map.entry;

import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.springturkey.database.entity.enumerable.CategoryType;
import devs.mrp.springturkey.database.service.DeltaFacadeService;
import devs.mrp.springturkey.delta.DeltaTable;
import devs.mrp.springturkey.delta.validation.DataConstrainerTemplate;
import devs.mrp.springturkey.delta.validation.FieldValidator;

@Service("activityConstraints")
public class ActivityDataConstrainer extends DataConstrainerTemplate {

	@Autowired
	private DeltaFacadeService deltaFacadeService;

	private Pattern categoryTypePattern = Pattern.compile(regexFromEnum(CategoryType.class));

	@Override
	protected boolean isValidTable(DeltaTable table) {
		return DeltaTable.ACTIVITY.equals(table);
	}

	@Override
	protected Map<String, FieldValidator> getFieldMap() {
		return Map.ofEntries(
				entry("categoryType", FieldValidator.builder()
						.fieldName("category_type")
						.predicate(s -> categoryTypePattern.matcher(s).matches())
						.build()),
				entry("groupId", FieldValidator.builder()
						.fieldName("turkey_group")
						.predicate(s -> getUuidPattern().matcher(s).matches())
						.build()),
				entry("preventClosing", FieldValidator.builder()
						.fieldName("prevent_closing")
						.predicate(s -> getBooleanPattern().matcher(s).matches())
						.build())
				);
	}

	@Override
	protected DeltaFacadeService getDeltaFacadeService() {
		return deltaFacadeService;
	}

}
