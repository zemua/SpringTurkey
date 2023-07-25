package devs.mrp.springturkey.delta.validation.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import devs.mrp.springturkey.Exceptions.TurkeySurpriseException;
import devs.mrp.springturkey.Exceptions.WrongDataException;
import devs.mrp.springturkey.delta.DeltaType;
import devs.mrp.springturkey.delta.validation.DataConstrainer;
import devs.mrp.springturkey.delta.validation.DataConstrainerProvider;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DataConstrainerProviderImpl implements DataConstrainerProvider {

	@Autowired
	@Qualifier("modificationConstraints")
	private DataConstrainer modificationConstrainter;


	@Override
	public DataConstrainer getFor(DeltaType type) throws WrongDataException {
		if (type == null) {
			throw new WrongDataException("Invalid table");
		}
		switch (type) {
		case MODIFICATION:
			return modificationConstrainter;
		case CREATION:
			return null; // TODO contemplate case
		case DELETION:
			return null; // TODO contemplate case
		default:
			log.error("Enum case {} not contemplated", type);
			throw new TurkeySurpriseException("Enum case not contemplated");
		}
	}

}
