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
@ContextConfiguration(classes = {GroupDataConstrainer.class})
class GroupDataConstrainerTest {

	@MockBean
	private DeltaFacadeService deltaFacade;

	@Autowired
	@Qualifier("groupConstraints")
	private DataConstrainer dataConstrainer;

	@Test
	void pushesPreventCloseCorrectly() throws WrongDataException {
		UUID id = UUID.randomUUID();
		Delta delta = Delta.builder()
				.timestamp(LocalDateTime.of(2023, 2, 14, 4, 25))
				.deltaType(DeltaType.MODIFICATION)
				.table(DeltaTable.GROUP)
				.recordId(id)
				.fieldName("preventClose")
				.textValue("true")
				.build();

		Delta modifiedDelta = Delta.builder()
				.timestamp(LocalDateTime.of(2023, 2, 14, 4, 25))
				.deltaType(DeltaType.MODIFICATION)
				.table(DeltaTable.GROUP)
				.recordId(id)
				.fieldName("prevent_close")
				.textValue("true")
				.build();

		when(deltaFacade.pushDelta(ArgumentMatchers.refEq(modifiedDelta))).thenReturn(1);

		int result = dataConstrainer.pushDelta(delta);
		assertEquals(1, result);
	}

	@Test
	void pushesPreventCloseFails() throws WrongDataException {
		UUID id = UUID.randomUUID();
		Delta delta = Delta.builder()
				.timestamp(LocalDateTime.of(2023, 2, 14, 4, 25))
				.deltaType(DeltaType.MODIFICATION)
				.table(DeltaTable.GROUP)
				.recordId(id)
				.fieldName("preventClose")
				.textValue("invalid")
				.build();

		assertThrows(WrongDataException.class, () -> dataConstrainer.pushDelta(delta));
	}

	@Test
	void pushesNameCorrectly() throws WrongDataException {
		UUID id = UUID.randomUUID();
		Delta delta = Delta.builder()
				.timestamp(LocalDateTime.of(2023, 2, 14, 4, 25))
				.deltaType(DeltaType.MODIFICATION)
				.table(DeltaTable.GROUP)
				.recordId(id)
				.fieldName("name")
				.textValue("some name with numbers 123")
				.build();

		Delta modifiedDelta = Delta.builder()
				.timestamp(LocalDateTime.of(2023, 2, 14, 4, 25))
				.deltaType(DeltaType.MODIFICATION)
				.table(DeltaTable.GROUP)
				.recordId(id)
				.fieldName("name")
				.textValue("some name with numbers 123")
				.build();

		when(deltaFacade.pushDelta(ArgumentMatchers.refEq(modifiedDelta))).thenReturn(1);

		int result = dataConstrainer.pushDelta(delta);
		assertEquals(1, result);
	}

	@Test
	void pushesNameFails() throws WrongDataException {
		UUID id = UUID.randomUUID();
		Delta delta = Delta.builder()
				.timestamp(LocalDateTime.of(2023, 2, 14, 4, 25))
				.deltaType(DeltaType.MODIFICATION)
				.table(DeltaTable.GROUP)
				.recordId(id)
				.fieldName("name")
				.textValue("invalid symbol {")
				.build();

		assertThrows(WrongDataException.class, () -> dataConstrainer.pushDelta(delta));
	}

	@Test
	void invalidField() {
		UUID id = UUID.randomUUID();
		Delta delta = Delta.builder()
				.timestamp(LocalDateTime.of(2023, 2, 14, 4, 25))
				.deltaType(DeltaType.MODIFICATION)
				.table(DeltaTable.GROUP)
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
				.table(DeltaTable.ACTIVITY)
				.recordId(id)
				.fieldName("preventClose")
				.textValue("true")
				.build();

		assertThrows(WrongDataException.class, () -> dataConstrainer.pushDelta(delta));
	}

}
