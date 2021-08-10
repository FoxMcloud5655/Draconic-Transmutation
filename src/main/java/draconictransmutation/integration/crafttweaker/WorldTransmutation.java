package draconictransmutation.integration.crafttweaker;

import org.openzen.zencode.java.ZenCodeType;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;

import draconictransmutation.integration.crafttweaker.actions.WorldTransmuteAction;
import net.minecraft.block.BlockState;

@ZenRegister
@ZenCodeType.Name("mods.projecte.WorldTransmutation")
public class WorldTransmutation {

	@ZenCodeType.Method
	public static void add(BlockState input, BlockState output, @ZenCodeType.Optional BlockState sneakOutput) {
		CraftTweakerAPI.apply(new WorldTransmuteAction.Add(input, output, sneakOutput));
	}

	@ZenCodeType.Method
	public static void remove(BlockState input, BlockState output, @ZenCodeType.Optional BlockState sneakOutput) {
		CraftTweakerAPI.apply(new WorldTransmuteAction.Remove(input, output, sneakOutput));
	}

	@ZenCodeType.Method
	public static void removeAll() {
		CraftTweakerAPI.apply(new WorldTransmuteAction.RemoveAll());
	}
}