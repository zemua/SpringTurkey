package devs.mrp.springturkey.database.repository.dao;

import java.util.Map;

import devs.mrp.springturkey.delta.Delta;

public interface EntityFromDeltaDao {

	public int save(Delta delta, Map<String,String> entityMap);

}
