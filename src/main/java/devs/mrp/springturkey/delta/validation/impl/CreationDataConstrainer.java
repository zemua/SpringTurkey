package devs.mrp.springturkey.delta.validation.impl;

import org.springframework.stereotype.Service;

import devs.mrp.springturkey.Exceptions.WrongDataException;
import devs.mrp.springturkey.delta.Delta;
import devs.mrp.springturkey.delta.validation.DataConstrainer;

@Service("creationConstraints")
public class CreationDataConstrainer implements DataConstrainer {

	@Override
	public int pushDelta(Delta delta) throws WrongDataException {
		// TODO Auto-generated method stub
		return 0;
	}

}
