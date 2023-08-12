package devs.mrp.springturkey.database.service.impl;

import org.springframework.stereotype.Service;

import devs.mrp.springturkey.database.service.DeltaFacadeService;
import devs.mrp.springturkey.delta.Delta;

@Service
public class DeltaFacadeServiceImpl implements DeltaFacadeService {

	@Override
	public int pushCreation(Delta delta) {

		return 0;
	}

	@Override
	public int pushModification(Delta delta) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int pushDeletion(Delta delta) {
		// TODO Auto-generated method stub
		return 0;
	}

}
