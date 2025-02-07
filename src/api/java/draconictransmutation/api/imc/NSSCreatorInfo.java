package draconictransmutation.api.imc;

import draconictransmutation.api.nss.NSSCreator;

public class NSSCreatorInfo {

	private final String key;
	private final NSSCreator creator;

	/**
	 * @param key     Key that goes before the | to represent the given {@link NSSCreator} for JSON deserialization
	 * @param creator A creator to parse a {@link String} read from JSON and return a {@link draconictransmutation.api.nss.NormalizedSimpleStack}
	 */
	public NSSCreatorInfo(String key, NSSCreator creator) {
		this.key = key;
		this.creator = creator;
	}

	public String getKey() {
		return key;
	}

	public NSSCreator getCreator() {
		return creator;
	}
}