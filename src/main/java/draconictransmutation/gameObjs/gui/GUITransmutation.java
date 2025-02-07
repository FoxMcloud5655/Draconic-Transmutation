package draconictransmutation.gameObjs.gui;

import java.math.BigInteger;
import java.util.Locale;

import javax.annotation.Nonnull;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import draconictransmutation.DTCore;
import draconictransmutation.gameObjs.container.TransmutationContainer;
import draconictransmutation.gameObjs.container.inventory.TransmutationInventory;
import draconictransmutation.utils.Constants;
import draconictransmutation.utils.TransmutationEMCFormatter;
import draconictransmutation.utils.text.DTLang;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class GUITransmutation extends PEContainerScreen<TransmutationContainer> {

	private static final ResourceLocation texture = DTCore.rl("textures/gui/transmute.png");
	private final TransmutationInventory inv;
	private TextFieldWidget textBoxFilter;

	public GUITransmutation(TransmutationContainer container, PlayerInventory invPlayer, ITextComponent title) {
		super(container, invPlayer, title);
		this.inv = container.transmutationInventory;
		this.xSize = 228;
		this.ySize = 196;
		this.titleX = 6;
		this.titleY = 8;
	}

	@Override
	public void init() {
		super.init();

		this.textBoxFilter = new TextFieldWidget(this.font, guiLeft + 88, guiTop + 8, 45, 10, StringTextComponent.EMPTY);
		this.textBoxFilter.setText(inv.filter);

		addButton(new Button(guiLeft + 125, guiTop + 100, 14, 14, new StringTextComponent("<"), b -> {
			if (inv.searchpage != 0) {
				inv.searchpage--;
			}
			inv.filter = textBoxFilter.getText().toLowerCase(Locale.ROOT);
			inv.updateClientTargets();
		}));
		addButton(new Button(guiLeft + 193, guiTop + 100, 14, 14, new StringTextComponent(">"), b -> {
			if (inv.getKnowledgeSize() > 12) {
				inv.searchpage++;
			}
			inv.filter = textBoxFilter.getText().toLowerCase(Locale.ROOT);
			inv.updateClientTargets();
		}));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(@Nonnull MatrixStack matrix, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.color4f(1F, 1F, 1F, 1F);
		Minecraft.getInstance().textureManager.bindTexture(texture);
		blit(matrix, guiLeft, guiTop, 0, 0, xSize, ySize);
		this.textBoxFilter.render(matrix, mouseX, mouseY, partialTicks);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(@Nonnull MatrixStack matrix, int x, int y) {
		this.font.func_243248_b(matrix, title, titleX, titleY, 0x404040);
		//Don't render inventory as we don't have space
		BigInteger emcAmount = inv.getAvailableEmc();
		this.font.func_243248_b(matrix, DTLang.EMC_TOOLTIP.translate(""), 6, this.ySize - 104, 0x404040);
		ITextComponent emc = TransmutationEMCFormatter.formatEMC(emcAmount);
		this.font.func_243248_b(matrix, emc, 6, this.ySize - 94, 0x404040);

		if (inv.learnFlag > 0) {
			this.font.func_243248_b(matrix, DTLang.TRANSMUTATION_LEARNED_1.translate(), 98, 30, 0x404040);
			this.font.func_243248_b(matrix, DTLang.TRANSMUTATION_LEARNED_2.translate(), 99, 38, 0x404040);
			this.font.func_243248_b(matrix, DTLang.TRANSMUTATION_LEARNED_3.translate(), 100, 46, 0x404040);
			this.font.func_243248_b(matrix, DTLang.TRANSMUTATION_LEARNED_4.translate(), 101, 54, 0x404040);
			this.font.func_243248_b(matrix, DTLang.TRANSMUTATION_LEARNED_5.translate(), 102, 62, 0x404040);
			this.font.func_243248_b(matrix, DTLang.TRANSMUTATION_LEARNED_6.translate(), 103, 70, 0x404040);
			this.font.func_243248_b(matrix, DTLang.TRANSMUTATION_LEARNED_7.translate(), 104, 78, 0x404040);
			this.font.func_243248_b(matrix, DTLang.TRANSMUTATION_LEARNED_8.translate(), 107, 86, 0x404040);

			inv.learnFlag--;
		}

		if (inv.unlearnFlag > 0) {
			this.font.func_243248_b(matrix, DTLang.TRANSMUTATION_UNLEARNED_1.translate(), 97, 22, 0x404040);
			this.font.func_243248_b(matrix, DTLang.TRANSMUTATION_UNLEARNED_2.translate(), 98, 30, 0x404040);
			this.font.func_243248_b(matrix, DTLang.TRANSMUTATION_UNLEARNED_3.translate(), 99, 38, 0x404040);
			this.font.func_243248_b(matrix, DTLang.TRANSMUTATION_UNLEARNED_4.translate(), 100, 46, 0x404040);
			this.font.func_243248_b(matrix, DTLang.TRANSMUTATION_UNLEARNED_5.translate(), 101, 54, 0x404040);
			this.font.func_243248_b(matrix, DTLang.TRANSMUTATION_UNLEARNED_6.translate(), 102, 62, 0x404040);
			this.font.func_243248_b(matrix, DTLang.TRANSMUTATION_UNLEARNED_7.translate(), 103, 70, 0x404040);
			this.font.func_243248_b(matrix, DTLang.TRANSMUTATION_UNLEARNED_8.translate(), 104, 78, 0x404040);
			this.font.func_243248_b(matrix, DTLang.TRANSMUTATION_UNLEARNED_9.translate(), 107, 86, 0x404040);

			inv.unlearnFlag--;
		}
	}

	@Override
	public void tick() {
		super.tick();
		this.textBoxFilter.tick();
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (textBoxFilter.isFocused()) {
			//Manually make it so that hitting escape when the filter is focused will exit the focus
			if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
				textBoxFilter.setFocused2(false);
				return true;
			}
			//Otherwise have it handle the key press
			//This is where key combos and deletion is handled
			if (textBoxFilter.keyPressed(keyCode, scanCode, modifiers)) {
				//If the filter reacted from the key press, then something happened and we should update the filter
				updateFilter();
				return true;
			}
			return false;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char c, int keyCode) {
		if (textBoxFilter.isFocused()) {
			//If our filter is focused have it handle the character being typed
			//This is where adding characters is handled
			if (textBoxFilter.charTyped(c, keyCode)) {
				//If the filter reacted from to a character being typed, then something happened and we should update the filter
				updateFilter();
				return true;
			}
			return false;
		}
		return super.charTyped(c, keyCode);
	}

	private void updateFilter() {
		String search = textBoxFilter.getText().toLowerCase(Locale.ROOT);
		if (!inv.filter.equals(search)) {
			inv.filter = search;
			inv.searchpage = 0;
			inv.updateClientTargets();
		}
	}

	@Override
	public boolean mouseClicked(double x, double y, int mouseButton) {
		int minX = textBoxFilter.x;
		int minY = textBoxFilter.y;
		int maxX = minX + textBoxFilter.getWidth();
		int maxY = minY + textBoxFilter.getHeightRealms();

		if (x >= minX && x <= maxX && y <= maxY) {
			if (mouseButton == 1) {
				inv.filter = "";
				inv.searchpage = 0;
				inv.updateClientTargets();
				this.textBoxFilter.setText("");
			}
			return this.textBoxFilter.mouseClicked(x, y, mouseButton);
		}
		return super.mouseClicked(x, y, mouseButton);
	}

	@Override
	public void onClose() {
		super.onClose();
		inv.learnFlag = 0;
		inv.unlearnFlag = 0;
	}

	@Override
	protected void renderHoveredTooltip(@Nonnull MatrixStack matrix, int mouseX, int mouseY) {
		BigInteger emcAmount = inv.getAvailableEmc();

		if (emcAmount.compareTo(Constants.MAX_EXACT_TRANSMUTATION_DISPLAY) < 0) {
			super.renderHoveredTooltip(matrix, mouseX, mouseY);
			return;
		}

		int emcLeft = guiLeft;
		int emcRight = emcLeft + 82;
		int emcTop = 95 + guiTop;
		int emcBottom = emcTop + 15;

		if (mouseX > emcLeft && mouseX < emcRight && mouseY > emcTop && mouseY < emcBottom) {
			renderTooltip(matrix, DTLang.EMC_TOOLTIP.translate(Constants.EMC_FORMATTER.format(emcAmount)), mouseX, mouseY);
		} else {
			super.renderHoveredTooltip(matrix, mouseX, mouseY);
		}
	}
}