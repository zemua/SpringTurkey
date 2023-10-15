package devs.mrp.springturkey.validation.service;

import devs.mrp.springturkey.Exceptions.WrongDataException;
import devs.mrp.springturkey.delta.Delta;

public interface ValidationService {

	public void validate(Delta delta) throws WrongDataException;

}
