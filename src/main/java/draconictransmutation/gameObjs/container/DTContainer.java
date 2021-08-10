package draconictransmutation.gameObjs.container;

import java.util.ArrayList;
import java.util.List;

import draconictransmutation.network.PacketHandler;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.util.IntReferenceHolder;

public abstract class DTContainer extends Container {

	// Vanilla only syncs int fields in the superclass as shorts (yay legacy)
	// here we hold fields we really want to use 32 bits for
	protected final List<IntReferenceHolder> intFields = new ArrayList<>();
	protected final List<BoxedLong> longFields = new ArrayList<>();

	protected DTContainer(ContainerType<?> type, int id) {
		super(type, id);
	}

	public final void updateProgressBarLong(int idx, long data) {
		longFields.get(idx).set(data);
	}

	public final void updateProgressBarInt(int idx, int data) {
		intFields.get(idx).set(data);
	}

	@Override
	public void detectAndSendChanges() {
		for (int i = 0; i < longFields.size(); i++) {
			if (longFields.get(i).isDirty()) {
				for (IContainerListener listener : listeners) {
					PacketHandler.sendProgressBarUpdateLong(listener, this, i, longFields.get(i).get());
				}
			}
		}

		for (int i = 0; i < intFields.size(); i++) {
			if (intFields.get(i).isDirty()) {
				for (IContainerListener listener : listeners) {
					PacketHandler.sendProgressBarUpdateInt(listener, this, i, intFields.get(i).get());
				}
			}
		}
		super.detectAndSendChanges();
	}

	public static class BoxedLong {

		private long inner;
		private boolean dirty = false;

		public long get() {
			return inner;
		}

		public void set(long v) {
			if (v != inner) {
				inner = v;
				dirty = true;
			}
		}

		public boolean isDirty() {
			boolean ret = dirty;
			dirty = false;
			return ret;
		}
	}
}