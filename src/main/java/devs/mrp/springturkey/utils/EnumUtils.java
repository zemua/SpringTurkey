package devs.mrp.springturkey.utils;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class EnumUtils {

	public static final <T extends Enum<T>> Predicate<String> getEnumPredicate(Class<T> enumerable) {
		List<Enum<T>> types = Arrays.asList(enumerable.getEnumConstants());
		List<String> enumNames = types.stream().map(Enum::name).toList();
		return enumNames::contains;
	}

}
