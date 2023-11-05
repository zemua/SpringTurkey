package devs.mrp.springturkey.validation.service;

import devs.mrp.springturkey.delta.Delta;
import devs.mrp.springturkey.exceptions.WrongDataException;

public interface ValidationService {

	public void validate(Delta delta) throws WrongDataException;

}
