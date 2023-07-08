package devs.mrp.springturkey.database.entity.enumerable;

public enum CategoryType {

	POSITIVE, NEGATIVE, NEUTRAL, UNCATEGORIZED;

	public static String regexValidator() {
		StringBuilder builder = new StringBuilder();
		CategoryType[] types = values();
		for (int i = 0; i < types.length; i++) {
			if (i>0) {
				builder.append("|");
			}
			builder.append("^" + types[i].name() + "$");
		}
		return builder.toString();
	}

}
