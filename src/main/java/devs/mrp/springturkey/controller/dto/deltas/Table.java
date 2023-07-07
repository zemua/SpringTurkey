package devs.mrp.springturkey.controller.dto.deltas;

import static java.util.Map.entry;

import java.util.Map;

public enum Table {

	// TODO add method to return a Service of each type implementing an interface DeltaModification
	// TODO add whitelist filter of fields that can be received
	// TODO add mapping from fieldName to actual object field

	GROUP(Map.ofEntries(entry("name","name"), entry("preventClose", "prevent_close")), null),
	ACTIVITY(Map.ofEntries(entry("categoryType", "category_type"), entry("groupId", "turkey_group"), entry("preventClosing", "preventClosing")), null),
	CONDITION(null, null),
	SETTING(null, null);

	private Map<String,String> fieldMap;

	private Table(Map<String,String> fields, Object service) {
		this.fieldMap = fields;
	}

}
