package devs.mrp.springturkey.database.service;

import devs.mrp.springturkey.delta.validation.ModificationDelta;

public interface DeltaFacade {

	public int pushDelta(ModificationDelta delta);

}
