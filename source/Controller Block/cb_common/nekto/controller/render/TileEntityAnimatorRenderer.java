package nekto.controller.render;

import nekto.controller.core.Controller;
import nekto.controller.ref.GeneralRef;
import nekto.controller.render.model.ModelAnimator2;
import nekto.controller.tile.TileEntityBase;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

public class TileEntityAnimatorRenderer extends TileEntitySpecialRenderer {
	private final ModelAnimator2 model;

	public TileEntityAnimatorRenderer() {
		this.model = new ModelAnimator2();
	}

	@Override
	public void func_147500_a(TileEntity tileEntity, double x, double y, double z, float scale) {
		GL11.glPushMatrix();
		setLighting(Minecraft.getMinecraft().theWorld, tileEntity.field_145851_c, tileEntity.field_145848_d, tileEntity.field_145849_e, Controller.animator);
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		//bindTextureByName(GeneralRef.FULL_TEXTURE_PATH+((TileEntityBase)tileEntity).getTexture());
        func_147499_a(new ResourceLocation(GeneralRef.FULL_TEXTURE_PATH + ((TileEntityBase<?>) tileEntity).getTexture()));
		GL11.glPushMatrix();
		GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
		this.model.render(0.0625F);
		renderOrb((TileEntityBase<?>) tileEntity);
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}

	private void renderOrb(TileEntityBase<?> tile) {
		GL11.glRotatef(tile.getRotation(), 0.0F, 1.0F, 0.0F);
		float f2 = MathHelper.sin(tile.getHoverHeight() / 10.0F) * 0.04F;
		GL11.glTranslatef(0.0F, f2, 0.0F);
		this.model.renderOrb(0.0625F);
	}

	public void setLighting(World world, int i, int j, int k, Block block) {
		Tessellator tessellator = Tessellator.instance;
		float f = world.getLightBrightness(i, j, k);
		int l = world.getLightBrightnessForSkyBlocks(i, j, k, 0);
		int l1 = l % 65536;
		int l2 = l / 65536;
		tessellator.setColorOpaque_F(f, f, f);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, l1, l2);
	}
}
