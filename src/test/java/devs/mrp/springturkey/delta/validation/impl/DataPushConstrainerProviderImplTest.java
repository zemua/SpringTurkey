package devs.mrp.springturkey.delta.validation.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.springturkey.database.service.DeltaServiceFacade;
import devs.mrp.springturkey.delta.DeltaType;
import devs.mrp.springturkey.delta.validation.DataPushConstrainer;
import devs.mrp.springturkey.exceptions.WrongDataException;
import devs.mrp.springturkey.utils.impl.ObjectMapperProvider;
import jakarta.validation.Validator;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DataPushConstrainerProviderImpl.class, CreationDataConstrainer.class, ModificationDeltaFilterService.class, DeletionDataConstrainer.class, ObjectMapperProvider.class})
class DataPushConstrainerProviderImplTest {

	@MockBean
	private Validator validator;

	@MockBean
	private DeltaServiceFacade deltaFacadeService;

	@Autowired
	private DataPushConstrainerProviderImpl provider;

	@Test
	void testMapsInstancesCorrectly() throws WrongDataException {
		DataPushConstrainer constrainer;

		constrainer = provider.getFor(DeltaType.CREATION);
		assertTrue(constrainer instanceof CreationDataConstrainer);

		constrainer = provider.getFor(DeltaType.DELETION);
		assertTrue(constrainer instanceof DeletionDataConstrainer);

		constrainer = provider.getFor(DeltaType.MODIFICATION);
		assertTrue(constrainer instanceof ModificationDeltaFilterService);
	}

	@Test
	void testNull() {
		assertThrows(WrongDataException.class, () -> provider.getFor(null));
	}

}
