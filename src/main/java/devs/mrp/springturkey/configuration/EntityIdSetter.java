package devs.mrp.springturkey.configuration;

import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertCallback;

import devs.mrp.springturkey.database.entity.intf.UuidIdentifiedEntity;

@Configuration
public class EntityIdSetter {

	@Bean
	public BeforeConvertCallback<UuidIdentifiedEntity> beforeSaveCallback() {
		return (entity, collection) -> {

			if (entity.getId() == null) {
				entity.setId(UUID.randomUUID());
			}
			return entity;
		};
	}

}
