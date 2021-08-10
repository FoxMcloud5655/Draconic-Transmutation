package draconictransmutation.network;

import org.apache.maven.artifact.versioning.ComparableVersion;

import draconictransmutation.DTCore;
import draconictransmutation.utils.text.DTLang;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLConfig;
import net.minecraftforge.forgespi.language.IModInfo;

@Mod.EventBusSubscriber(modid = DTCore.MODID, value = Dist.CLIENT)
public class ThreadCheckUpdate extends Thread {

	private static final String curseURL = "https://minecraft.curseforge.com/projects/draconic-transmutation/files";
	private static volatile ComparableVersion target = null;
	private static volatile boolean hasSentMessage = false;

	public ThreadCheckUpdate() {
		this.setName("Draconic Transmutation Update Checker Notifier");
	}

	@Override
	public void run() {
		if (!FMLConfig.runVersionCheck()) {
			//Forge update checker disabled, just exit
			return;
		}
		IModInfo info = DTCore.MOD_CONTAINER.getModInfo();
		VersionChecker.CheckResult result = null;

		int tries = 0;
		do {
			VersionChecker.CheckResult res = VersionChecker.getResult(info);
			if (res.status != VersionChecker.Status.PENDING) {
				result = res;
			}
			try {
				Thread.sleep(1000L);
			} catch (InterruptedException ignored) {
			}
			tries++;
		} while (result == null && tries < 10);

		if (result == null) {
			DTCore.LOGGER.info("Update check failed.");
			return;
		}

		if (result.status == VersionChecker.Status.OUTDATED) {
			target = result.target;
		}
	}

	@SubscribeEvent
	public static void worldLoad(EntityJoinWorldEvent evt) {
		if (evt.getEntity() instanceof ClientPlayerEntity && target != null && !hasSentMessage) {
			hasSentMessage = true;
			evt.getEntity().sendMessage(DTLang.UPDATE_AVAILABLE.translate(target), Util.DUMMY_UUID);
			evt.getEntity().sendMessage(DTLang.UPDATE_GET_IT.translate(), Util.DUMMY_UUID);

			ITextComponent link = new StringTextComponent(curseURL);
			link.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, curseURL));
			evt.getEntity().sendMessage(link, Util.DUMMY_UUID);
		}
	}
}