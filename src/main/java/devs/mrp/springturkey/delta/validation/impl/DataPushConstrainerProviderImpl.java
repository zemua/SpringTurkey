package devs.mrp.springturkey.delta.validation.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import devs.mrp.springturkey.delta.DeltaType;
import devs.mrp.springturkey.delta.validation.DataPushConstrainer;
import devs.mrp.springturkey.delta.validation.DataPushConstrainerProvider;
import devs.mrp.springturkey.exceptions.TurkeySurpriseException;
import devs.mrp.springturkey.exceptions.WrongDataException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DataPushConstrainerProviderImpl implements DataPushConstrainerProvider {

	@Autowired
	@Qualifier("modificationConstraints")
	private DataPushConstrainer modificationConstrainter;

	@Autowired
	@Qualifier("creationConstraints")
	private DataPushConstrainer creationConstrainter;

	@Autowired
	@Qualifier("deletionConstraints")
	private DataPushConstrainer deletionConstrainer;


	@Override
	public DataPushConstrainer getFor(DeltaType type) throws WrongDataException {
		if (type == null) {
			throw new WrongDataException("Invalid table");
		}
		switch (type) {
		case MODIFICATION:
			return modificationConstrainter;
		case CREATION:
			return creationConstrainter;
		case DELETION:
			return deletionConstrainer;
		default:
			log.error("Enum case {} not contemplated", type);
			throw new TurkeySurpriseException("Enum case not contemplated");
		}
	}

}
