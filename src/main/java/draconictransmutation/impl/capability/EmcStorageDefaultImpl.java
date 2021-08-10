package draconictransmutation.impl.capability;

import draconictransmutation.api.capabilities.tile.IEmcStorage;
import draconictransmutation.utils.Constants;

public final class EmcStorageDefaultImpl implements IEmcStorage {

	private long currentEMC;

	@Override
	public long getStoredEmc() {
		return currentEMC;
	}

	@Override
	public long getMaximumEmc() {
		return Constants.TILE_MAX_EMC;
	}

	@Override
	public long extractEmc(long toExtract, EmcAction action) {
		if (toExtract < 0) {
			return insertEmc(-toExtract, action);
		}
		long toRemove = Math.min(getStoredEmc(), toExtract);
		if (action.execute()) {
			currentEMC -= toRemove;
		}
		return toRemove;
	}

	@Override
	public long insertEmc(long toAccept, EmcAction action) {
		if (toAccept < 0) {
			return extractEmc(-toAccept, action);
		}
		long toAdd = Math.min(getNeededEmc(), toAccept);
		if (action.execute()) {
			currentEMC += toAdd;
		}
		return toAdd;
	}
}