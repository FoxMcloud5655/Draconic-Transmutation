package draconictransmutation.integration.top;

import java.util.function.Function;

import draconictransmutation.DTCore;
import draconictransmutation.config.DTConfig;
import draconictransmutation.utils.EMCHelper;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

//Registered via IMC
@SuppressWarnings("unused")
public class DTProbeInfoProvider implements IProbeInfoProvider, Function<ITheOneProbe, Void> {

	@Override
	public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
		if (DTConfig.server.misc.hwylaTOPDisplay.get()) {
			long value = EMCHelper.getEmcValue(new ItemStack(blockState.getBlock()));
			if (value > 0) {
				probeInfo.text(EMCHelper.getEmcTextComponent(value, 1));
			}
		}
	}

	@Override
	public String getID() {
		return DTCore.MODID + ":emc";
	}

	@Override
	public Void apply(ITheOneProbe iTheOneProbe) {
		iTheOneProbe.registerProvider(this);
		return null;
	}
}