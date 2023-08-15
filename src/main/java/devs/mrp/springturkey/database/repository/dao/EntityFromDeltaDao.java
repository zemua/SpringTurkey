package devs.mrp.springturkey.database.repository.dao;

import devs.mrp.springturkey.delta.Delta;

public interface EntityFromDeltaDao {

	public int save(Delta delta);

}
