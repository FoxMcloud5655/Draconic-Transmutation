package draconictransmutation.integration.top;

import draconictransmutation.integration.IntegrationHelper;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;

public class TOPIntegration {

	public static void sendIMC(InterModEnqueueEvent event) {
		InterModComms.sendTo(IntegrationHelper.TOP_MODID, "getTheOneProbe", DTProbeInfoProvider::new);
	}
}