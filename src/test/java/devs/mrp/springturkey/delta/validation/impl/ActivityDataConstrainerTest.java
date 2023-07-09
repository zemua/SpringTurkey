package devs.mrp.springturkey.delta.validation.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.springturkey.Exceptions.WrongDataException;
import devs.mrp.springturkey.database.service.DeltaFacadeService;
import devs.mrp.springturkey.delta.validation.DataConstrainer;
import devs.mrp.springturkey.delta.validation.ModificationDelta;
import devs.mrp.springturkey.delta.validation.Table;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ActivityDataConstrainer.class})
class ActivityDataConstrainerTest {

	@MockBean
	private DeltaFacadeService deltaFacade;

	@Autowired
	@Qualifier("activityConstraints")
	private DataConstrainer dataConstrainer;

	@Test
	void pushesCategoryTypeCorrectly() throws WrongDataException {
		UUID id = UUID.randomUUID();
		ModificationDelta delta = ModificationDelta.builder()
				.timestamp(LocalDateTime.of(2023, 2, 14, 4, 25))
				.table(Table.ACTIVITY)
				.recordId(id)
				.fieldName("categoryType")
				.textValue("POSITIVE")
				.build();

		ModificationDelta modifiedDelta = delta.withFieldName("category_type");

		when(deltaFacade.pushDelta(ArgumentMatchers.refEq(modifiedDelta))).thenReturn(1);

		int result = dataConstrainer.pushDelta(delta);
		assertEquals(1, result);
	}

	@Test
	void pushesCategoryTypeFails() throws WrongDataException {
		UUID id = UUID.randomUUID();
		ModificationDelta delta = ModificationDelta.builder()
				.timestamp(LocalDateTime.of(2023, 2, 14, 4, 25))
				.table(Table.ACTIVITY)
				.recordId(id)
				.fieldName("categoryType")
				.textValue("invalid")
				.build();

		assertThrows(WrongDataException.class, () -> dataConstrainer.pushDelta(delta));
	}

	@Test
	void test() {
		fail("Not yet implemented");
	}

}
