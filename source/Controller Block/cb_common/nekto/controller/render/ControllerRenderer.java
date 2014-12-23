package nekto.controller.render;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import nekto.controller.block.BlockBase;
import nekto.controller.tile.TileEntityBase;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public final class ControllerRenderer implements ISimpleBlockRenderingHandler {
    @Override
    public void renderInventoryBlock(Block block, int i, int j, RenderBlocks renderblocks) {
        if (block instanceof BlockBase) {
            TileEntityRendererDispatcher.instance.getSpecialRendererByClass(TileEntityBase.class).renderTileEntityAt(block.createTileEntity(Minecraft.getMinecraft().theWorld, 0), 0.0D, 0.0D, 0.0D, 0.0F);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        }
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess iblockaccess, int i, int j, int k, Block block, int l, RenderBlocks renderblocks) {
        return false;
    }

    @Override
    public boolean shouldRender3DInInventory(int i) {
        return true;
    }

    @Override
    public int getRenderId() {
        return BlockBase.renderID;
    }
}
