package devs.mrp.springturkey.database.service;

import devs.mrp.springturkey.delta.Delta;

public interface DeltaFacadeService {

	public int pushModification(Delta delta);

	public int pushCreation(Delta delta);

	public int pushDeletion(Delta delta);

}
