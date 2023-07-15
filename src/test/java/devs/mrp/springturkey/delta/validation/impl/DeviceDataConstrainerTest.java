package devs.mrp.springturkey.delta.validation.impl;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.springturkey.Exceptions.WrongDataException;
import devs.mrp.springturkey.database.service.DeltaFacadeService;
import devs.mrp.springturkey.delta.DeltaTable;
import devs.mrp.springturkey.delta.DeltaType;
import devs.mrp.springturkey.delta.validation.DataConstrainer;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DeviceDataConstrainer.class})
class DeviceDataConstrainerTest {

	@MockBean
	private DeltaFacadeService deltaFacade;

	@Autowired
	@Qualifier("deviceConstraints")
	private DataConstrainer dataConstrainer;

	private static Stream<Arguments> provideCorrectValues() {
		return Stream.of(

				);
	}

	@ParameterizedTest
	@MethodSource("provideCorrectValues")
	void pushesCorrectly(DeltaType deltaType, DeltaTable table, String fieldName, String columnName, String textValue) throws WrongDataException {
		fail("not yet implemented");
	}

	private static Stream<Arguments> provideIncorrectValues() {
		// this doesn't throw exception
		// Arguments.of(DeltaType.MODIFICATION, DeltaTable.CONDITION, "requiredUsageMs", "12345")
		return Stream.of(

				);
	}

	@ParameterizedTest
	@MethodSource("provideIncorrectValues")
	void pushesFails(DeltaType deltaType, DeltaTable table, String fieldName, String textValue) throws WrongDataException {
		fail("not yet implemented");
	}

}
