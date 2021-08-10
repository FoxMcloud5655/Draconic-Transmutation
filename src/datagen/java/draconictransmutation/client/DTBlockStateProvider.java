package draconictransmutation.client;

import java.util.function.Function;

import draconictransmutation.DTCore;
import draconictransmutation.gameObjs.registration.impl.BlockRegistryObject;
import draconictransmutation.gameObjs.registries.DTBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.Property;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class DTBlockStateProvider extends BlockStateProvider {

	public DTBlockStateProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
		super(generator, DTCore.MODID, existingFileHelper);
	}

	@Override
	protected void registerStatesAndModels() {
		registerTransmutationTable();
	}

	private void particleOnly(BlockRegistryObject<?, ?> block) {
		String name = getName(block);
		simpleBlock(block.getBlock(), models().getBuilder(name).texture("particle", modLoc("block/" + name)));
	}

	private void registerTransmutationTable() {
		ResourceLocation top = modLoc("block/transmutation_stone/top");
		BlockModelBuilder model = models()
				.withExistingParent(getName(DTBlocks.TRANSMUTATION_TABLE), "block/block")
				.texture("bottom", modLoc("block/transmutation_stone/bottom"))
				.texture("top", top)
				.texture("side", modLoc("block/transmutation_stone/side"))
				.texture("particle", top)
				.element()
				.from(0, 0, 0)
				.to(16, 4, 16)
				.face(Direction.DOWN).texture("#bottom").cullface(Direction.DOWN).end()
				.face(Direction.UP).texture("#top").end()
				.face(Direction.NORTH).texture("#side").cullface(Direction.NORTH).end()
				.face(Direction.SOUTH).texture("#side").cullface(Direction.SOUTH).end()
				.face(Direction.WEST).texture("#side").cullface(Direction.WEST).end()
				.face(Direction.EAST).texture("#side").cullface(Direction.EAST).end()
				.end();
		directionalBlock(DTBlocks.TRANSMUTATION_TABLE.getBlock(), state -> model, 180, BlockStateProperties.WATERLOGGED);
	}

	private void registerTieredOrientable(String type, BlockRegistryObject<?, ?> base, BlockRegistryObject<?, ?> mk2, BlockRegistryObject<?, ?> mk3) {
		ResourceLocation side = modLoc("block/" + type + "/other");
		BlockModelBuilder model = models().orientableWithBottom(getName(base), side, modLoc("block/" + type + "/front"), side,
				modLoc("block/" + type + "/top_1"));
		horizontalBlock(base.getBlock(), model);
		horizontalBlock(mk2.getBlock(), models().getBuilder(getName(mk2))
				.parent(model)
				.texture("top", modLoc("block/" + type + "/top_2")));
		horizontalBlock(mk3.getBlock(), models().getBuilder(getName(mk3))
				.parent(model)
				.texture("top", modLoc("block/" + type + "/top_3")));
	}

	private void simpleBlocks(BlockRegistryObject<?, ?>... blocks) {
		for (BlockRegistryObject<?, ?> block : blocks) {
			simpleBlock(block.getBlock());
		}
	}

	private void directionalBlock(Block block, Function<BlockState, ModelFile> modelFunc, int angleOffset, Property<?>... toSkip) {
		getVariantBuilder(block).forAllStatesExcept(state -> {
			Direction dir = state.get(BlockStateProperties.FACING);
			return ConfiguredModel.builder()
					.modelFile(modelFunc.apply(state))
					.rotationX(dir == Direction.DOWN ? 180 : dir.getAxis().isHorizontal() ? 90 : 0)
					.rotationY(dir.getAxis().isVertical() ? 0 : (((int) dir.getHorizontalAngle()) + angleOffset) % 360)
					.build();
		}, toSkip);
	}

	private static String getName(IItemProvider itemProvider) {
		return itemProvider.asItem().getRegistryName().getPath();
	}
}