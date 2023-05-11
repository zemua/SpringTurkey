package devs.mrp.springturkey.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertCallback;

import devs.mrp.springturkey.database.entity.User;
import devs.mrp.springturkey.database.entity.intf.UuidIdentifiedEntity;

class EntityIdSetterTest {

	private EntityIdSetter setter = new EntityIdSetter();

	@Test
	void testNullGetsGenerated() {
		BeforeConvertCallback<UuidIdentifiedEntity> callback = setter.beforeSaveCallback();
		User user = User.builder().email("someEmail").build();
		callback.onBeforeConvert(user, null);
		assertNotNull(user.getId());
	}

	@Test
	void testExistingIdIsPreserved() {
		UUID generatedId = UUID.randomUUID();

		BeforeConvertCallback<UuidIdentifiedEntity> callback = setter.beforeSaveCallback();
		User user = User.builder().email("someEmail").id(generatedId).build();
		callback.onBeforeConvert(user, null);
		assertEquals(generatedId, user.getId());
	}

}
