package draconictransmutation.emc.mappers.customConversions.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.SerializedName;

import draconictransmutation.api.nss.NormalizedSimpleStack;

public class FixedValues {

	@SerializedName("before")
	public Map<NormalizedSimpleStack, Long> setValueBefore = new HashMap<>();
	@SerializedName("after")
	public Map<NormalizedSimpleStack, Long> setValueAfter = new HashMap<>();
	public List<CustomConversion> conversion = new ArrayList<>();

	public void merge(FixedValues other) {
		setValueBefore.putAll(other.setValueBefore);
		setValueAfter.putAll(other.setValueAfter);
		conversion.addAll(other.conversion);
	}
}