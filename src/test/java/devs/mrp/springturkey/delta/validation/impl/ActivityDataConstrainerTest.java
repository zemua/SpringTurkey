package devs.mrp.springturkey.delta.validation.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import devs.mrp.springturkey.delta.Delta;
import devs.mrp.springturkey.delta.DeltaTable;
import devs.mrp.springturkey.delta.DeltaType;
import devs.mrp.springturkey.delta.validation.DataConstrainer;

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
		Delta delta = Delta.builder()
				.timestamp(LocalDateTime.of(2023, 2, 14, 4, 25))
				.deltaType(DeltaType.MODIFICATION)
				.table(DeltaTable.ACTIVITY)
				.recordId(id)
				.fieldName("categoryType")
				.textValue("POSITIVE")
				.build();

		Delta modifiedDelta = delta.withFieldName("category_type");

		when(deltaFacade.pushDelta(ArgumentMatchers.refEq(modifiedDelta))).thenReturn(1);

		int result = dataConstrainer.pushDelta(delta);
		assertEquals(1, result);
	}

	@Test
	void pushesCategoryTypeFails() throws WrongDataException {
		UUID id = UUID.randomUUID();
		Delta delta = Delta.builder()
				.timestamp(LocalDateTime.of(2023, 2, 14, 4, 25))
				.deltaType(DeltaType.MODIFICATION)
				.table(DeltaTable.ACTIVITY)
				.recordId(id)
				.fieldName("categoryType")
				.textValue("invalid")
				.build();

		assertThrows(WrongDataException.class, () -> dataConstrainer.pushDelta(delta));
	}

	@Test
	void pushesGroupIdCorrectly() throws WrongDataException {
		UUID id = UUID.randomUUID();
		Delta delta = Delta.builder()
				.timestamp(LocalDateTime.of(2023, 2, 14, 4, 25))
				.deltaType(DeltaType.MODIFICATION)
				.table(DeltaTable.ACTIVITY)
				.recordId(id)
				.fieldName("groupId")
				.textValue(UUID.randomUUID().toString())
				.build();

		Delta modifiedDelta = delta.withFieldName("turkey_group");

		when(deltaFacade.pushDelta(ArgumentMatchers.refEq(modifiedDelta))).thenReturn(1);

		int result = dataConstrainer.pushDelta(delta);
		assertEquals(1, result);
	}

	@Test
	void pushedGroupIdFails() {
		UUID id = UUID.randomUUID();
		Delta delta = Delta.builder()
				.timestamp(LocalDateTime.of(2023, 2, 14, 4, 25))
				.deltaType(DeltaType.MODIFICATION)
				.table(DeltaTable.ACTIVITY)
				.recordId(id)
				.fieldName("groupId")
				.textValue("invalid")
				.build();

		assertThrows(WrongDataException.class, () -> dataConstrainer.pushDelta(delta));
	}

	@Test
	void pushesPreventClosingCorrectly() throws WrongDataException {
		UUID id = UUID.randomUUID();
		Delta delta = Delta.builder()
				.timestamp(LocalDateTime.of(2023, 2, 14, 4, 25))
				.deltaType(DeltaType.MODIFICATION)
				.table(DeltaTable.ACTIVITY)
				.recordId(id)
				.fieldName("preventClosing")
				.textValue("true")
				.build();

		Delta modifiedDelta = delta.withFieldName("prevent_closing");

		when(deltaFacade.pushDelta(ArgumentMatchers.refEq(modifiedDelta))).thenReturn(1);

		int result = dataConstrainer.pushDelta(delta);
		assertEquals(1, result);
	}

	@Test
	void pushesPreventClosingFails() throws WrongDataException {
		UUID id = UUID.randomUUID();
		Delta delta = Delta.builder()
				.timestamp(LocalDateTime.of(2023, 2, 14, 4, 25))
				.deltaType(DeltaType.MODIFICATION)
				.table(DeltaTable.ACTIVITY)
				.recordId(id)
				.fieldName("preventClosing")
				.textValue("invalid")
				.build();

		assertThrows(WrongDataException.class, () -> dataConstrainer.pushDelta(delta));
	}

	@Test
	void invalidField() {
		UUID id = UUID.randomUUID();
		Delta delta = Delta.builder()
				.timestamp(LocalDateTime.of(2023, 2, 14, 4, 25))
				.deltaType(DeltaType.MODIFICATION)
				.table(DeltaTable.ACTIVITY)
				.recordId(id)
				.fieldName("invalidField")
				.textValue("true")
				.build();

		assertThrows(WrongDataException.class, () -> dataConstrainer.pushDelta(delta));
	}

	@Test
	void invalidTable() {
		UUID id = UUID.randomUUID();
		Delta delta = Delta.builder()
				.timestamp(LocalDateTime.of(2023, 2, 14, 4, 25))
				.deltaType(DeltaType.MODIFICATION)
				.table(DeltaTable.CONDITION)
				.recordId(id)
				.fieldName("preventClosing")
				.textValue("true")
				.build();

		assertThrows(WrongDataException.class, () -> dataConstrainer.pushDelta(delta));
	}

}
