package draconictransmutation.gameObjs.registries;

import draconictransmutation.gameObjs.blocks.TransmutationStone;
import draconictransmutation.gameObjs.registration.impl.BlockDeferredRegister;
import draconictransmutation.gameObjs.registration.impl.BlockRegistryObject;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;

public class DTBlocks {
	public static final BlockDeferredRegister BLOCKS = new BlockDeferredRegister();
	public static final BlockRegistryObject<TransmutationStone, BlockItem> TRANSMUTATION_TABLE = BLOCKS.register("transmutation_table", () -> new TransmutationStone(AbstractBlock.Properties.create(Material.ROCK).setRequiresTool().hardnessAndResistance(10, 30)));
}