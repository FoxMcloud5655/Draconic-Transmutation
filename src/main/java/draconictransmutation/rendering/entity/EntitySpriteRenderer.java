package draconictransmutation.rendering.entity;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import draconictransmutation.rendering.DTRenderType;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Matrix4f;

public abstract class EntitySpriteRenderer<T extends Entity> extends EntityRenderer<T> {

	public EntitySpriteRenderer(EntityRendererManager manager) {
		super(manager);
	}

	@Override
	public void render(@Nonnull T entity, float entityYaw, float partialTick, @Nonnull MatrixStack matrix, @Nonnull IRenderTypeBuffer renderer, int light) {
		matrix.push();
		matrix.rotate(renderManager.getCameraOrientation());
		matrix.scale(0.5F, 0.5F, 0.5F);
		IVertexBuilder builder = renderer.getBuffer(DTRenderType.spriteRenderer(getEntityTexture(entity)));
		Matrix4f matrix4f = matrix.getLast().getMatrix();
		builder.pos(matrix4f, -1, -1, 0).tex(1, 1).endVertex();
		builder.pos(matrix4f, -1, 1, 0).tex(1, 0).endVertex();
		builder.pos(matrix4f, 1, 1, 0).tex(0, 0).endVertex();
		builder.pos(matrix4f, 1, -1, 0).tex(0, 1).endVertex();
		matrix.pop();
	}
}