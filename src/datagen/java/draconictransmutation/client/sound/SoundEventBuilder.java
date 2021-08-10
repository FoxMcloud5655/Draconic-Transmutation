package draconictransmutation.client.sound;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import draconictransmutation.gameObjs.registration.impl.SoundEventRegistryObject;
import draconictransmutation.utils.text.ILangEntry;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.util.ResourceLocation;

/**
 * @apiNote From Mekanism
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SoundEventBuilder {

	public static SoundEventBuilder create(SoundEventRegistryObject<?> soundEventRO) {
		return new SoundEventBuilder(soundEventRO);
	}

	private final String path;
	private boolean replace;
	@Nullable
	private String translationKey;
	private final Map<ResourceLocation, SoundBuilder> soundBuilders = new LinkedHashMap<>();

	private SoundEventBuilder(SoundEventRegistryObject<?> soundEventRO) {
		path = soundEventRO.get().getRegistryName().getPath();
	}

	public String getPath() {
		return path;
	}

	/**
	 * Only used in resource packs, but included here for completeness. If the sounds listed in sounds should replace the sounds listed in the default sounds.json for
	 * this sound event.
	 */
	public SoundEventBuilder replace() {
		this.replace = true;
		return this;
	}

	/**
	 * Will be translated as the subtitle of the sound if Show Subtitles is enabled in game.
	 *
	 * @apiNote Optional
	 */
	public SoundEventBuilder subtitle(ILangEntry langEntry) {
		this.translationKey = Objects.requireNonNull(langEntry).getTranslationKey();
		return this;
	}

	/**
	 * Helper method for {@link #addSounds(SoundBuilder...)}, for when all our sound options are the default
	 */
	public SoundEventBuilder addSounds(Function<ResourceLocation, SoundBuilder> builderFunction, ResourceLocation... locations) {
		for (ResourceLocation location : locations) {
			addSounds(builderFunction.apply(location));
		}
		return this;
	}

	public SoundEventBuilder addSounds(SoundBuilder... soundBuilders) {
		for (SoundBuilder soundBuilder : soundBuilders) {
			ResourceLocation location = soundBuilder.getLocation();
			if (this.soundBuilders.containsKey(location)) {
				throw new RuntimeException("Sound '" + location + "' has already been added to this sound event (" + getPath() + "). Increase the weight on the sound instead.");
			}
			this.soundBuilders.put(location, soundBuilder);
		}
		return this;
	}

	public JsonElement toJson() {
		JsonObject jsonObject = new JsonObject();
		if (replace) {
			jsonObject.addProperty("replace", true);
		}
		if (translationKey != null) {
			jsonObject.addProperty("subtitle", translationKey);
		}
		if (!soundBuilders.isEmpty()) {
			JsonArray sounds = new JsonArray();
			for (SoundBuilder soundBuilder : soundBuilders.values()) {
				sounds.add(soundBuilder.toJson());
			}
			jsonObject.add("sounds", sounds);
		}
		return jsonObject;
	}
}