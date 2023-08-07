package devs.mrp.springturkey.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class UuidUtilsTest {

	@Test
	void stringIsUuid() {
		String uuid = UUID.randomUUID().toString();
		assertTrue(UuidUtils.isUuid(uuid));
		assertFalse(UuidUtils.isUuid("not-an-uuid"));
		assertFalse(UuidUtils.isUuid(uuid + "q")); // 1 extra character
		assertFalse(UuidUtils.isUuid(uuid.substring(1))); // 1 missing character at start
		assertFalse(UuidUtils.isUuid(uuid.substring(0, uuid.length()-1))); // 1 missing character at end
		assertFalse(UuidUtils.isUuid(uuid.replace(uuid.charAt(3), '$'))); // 1 wrong character
		assertFalse(UuidUtils.isUuid(uuid.replace('-', 'a'))); // wrong slashes
	}

}
