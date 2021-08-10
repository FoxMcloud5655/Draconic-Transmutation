package draconictransmutation.integration.hwyla;

import java.util.List;

import draconictransmutation.config.DTConfig;
import draconictransmutation.utils.EMCHelper;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IDataAccessor;
import mcp.mobius.waila.api.IPluginConfig;
import net.minecraft.util.text.ITextComponent;

public class HwylaDataProvider implements IComponentProvider {

	static final HwylaDataProvider INSTANCE = new HwylaDataProvider();

	@Override
	public void appendBody(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
		if (DTConfig.server.misc.hwylaTOPDisplay.get()) {
			long value = EMCHelper.getEmcValue(accessor.getStack());
			if (value > 0) {
				tooltip.add(EMCHelper.getEmcTextComponent(value, 1));
			}
		}
	}
}