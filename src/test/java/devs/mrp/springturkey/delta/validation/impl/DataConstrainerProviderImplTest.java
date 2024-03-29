package devs.mrp.springturkey.delta.validation.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.springturkey.Exceptions.WrongDataException;
import devs.mrp.springturkey.database.service.DeltaFacadeService;
import devs.mrp.springturkey.delta.DeltaType;
import devs.mrp.springturkey.delta.validation.DataConstrainer;
import jakarta.validation.Validator;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DataConstrainerProviderImpl.class, CreationDataConstrainer.class, ModificationDataConstrainer.class, DeletionDataConstrainer.class})
class DataConstrainerProviderImplTest {

	@MockBean
	private Validator validator;

	@MockBean
	private DeltaFacadeService deltaFacadeService;

	@Autowired
	private DataConstrainerProviderImpl provider;

	@Test
	void testMapsInstancesCorrectly() throws WrongDataException {
		DataConstrainer constrainer;

		constrainer = provider.getFor(DeltaType.CREATION);
		assertTrue(constrainer instanceof CreationDataConstrainer);

		constrainer = provider.getFor(DeltaType.DELETION);
		assertTrue(constrainer instanceof DeletionDataConstrainer);

		constrainer = provider.getFor(DeltaType.MODIFICATION);
		assertTrue(constrainer instanceof ModificationDataConstrainer);
	}

	@Test
	void testNull() {
		assertThrows(WrongDataException.class, () -> provider.getFor(null));
	}

}
